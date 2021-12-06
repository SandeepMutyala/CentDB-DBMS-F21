package dao;

import Validations.DatabaseExists;
import Validations.DatatypeValidation;
import Validations.TableExistence;
import utils.*;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseOperationsImpl implements DatabaseOperations {
    /*
    * result=1, db successfully created
    * result=2, db already exists
    * result=3, Table created successfully
    * result=4, Table creation failed
    * result=5, Inserted Successfully
    * result=6, Insert Failed
    * result= 7, Select success
    * result= 8, Select failed
    * result= 9, update success
    * result = 10, update failed
    * result=11, delete row success
    * result=12, delete row failed
    * */
    static Formatter  fmt = new Formatter();
    @Override
    public  int createDb(String query){
        int result=0;
        String[] analyseQuery=query.split(" ");
        String dbPath = GlobalSessionDetails.loggedInUsername+"/";
        String directoryPath=dbPath.concat(analyseQuery[2]);

        File directory = new File(directoryPath);
        if (!directory.exists()){
            directory.mkdirs();
            CreateStructureAndDataExportFile.structureAndDataExportFileCreation(directoryPath);
            if(CreateStructureAndDataExportFile.insertInStructureAndDataExportFile(query,directoryPath)){
                result=1;
            }
        }
        return result;
    }

    @Override
    public int createTable(String query) {
        int result=0;
        //String tableDiectoryPath = GlobalSessionDetails.loggedInUsername + "/";
        String tablePath="";
        String dbName="";
        String tableName="";
        String[] columnName;
        String[] columnDataType;

        //Pattern to get table name and DBname
        Pattern tablePattern = Pattern.compile(Constants.DB_TABLE_NAME_CREATE_SEPARATOR_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matchResult = tablePattern.matcher(query);

        try{
            if (matchResult.find()) {
                String[] separateDbtableName=matchResult.group(1).split("\\.");

                // Primary key is must
                String[] columnDetails=matchResult.group(2).substring(1,matchResult.group(2).length()-1).split(",");
                String[] removedPrimaryKeyColumnDetails=new String[columnDetails.length-1];
                for(int pi=0;pi<columnDetails.length-1;pi++){
                    removedPrimaryKeyColumnDetails[pi]=columnDetails[pi];
                }
                columnDataType=fetchColumnDataType(removedPrimaryKeyColumnDetails);
                columnName=fetchColumnName(removedPrimaryKeyColumnDetails);


                if(DatatypeValidation.validateTableDataType(columnDataType)){

                    // if database name is not in the query
                    if(separateDbtableName.length<=1){
                        dbName=GlobalSessionDetails.getDbInAction();
                        tableName=matchResult.group(1).toLowerCase();
                        tablePath=GlobalSessionDetails.getLoggedInUsername().concat("/"+dbName+"/"+tableName)+".txt";
                    }
                    // if database name is in query
                    if(separateDbtableName.length>1){
                        dbName=separateDbtableName[0];
                        tableName=separateDbtableName[1];
                        tablePath=GlobalSessionDetails.getLoggedInUsername().concat("/"+dbName+"/"+tableName)+".txt";
                    }

                    if(!dbName.isEmpty()){
                        if(DatabaseExists.validateDatabaseExistence(dbName)){
                            //create Table
                            result=createTableFile(dbName,tablePath,tableName,columnDataType,columnName,query);
                        }
                    }
                    else{
                        result=4;
                        System.out.println("Either provide DB name or perform use db query");
                    }
                }
                else{
                    System.out.println("Please select valid datatype");
                }
            }
            else{
                System.out.println("Please check the syntax for table creation");
            }
        }catch(Exception ex){
            System.out.println("Please check the syntax for table creation"+ex);
            result=4;
        }finally {
            return result;
        }
    }

    public static String[] fetchColumnDataType(String[] columnDetails){
        String[] columnsDataType=new String[columnDetails.length];
        for(int i=0;i<columnDetails.length;i++){

            String[] columnData=columnDetails[i].trim().split(" ");
                columnsDataType[i]=columnData[1];
        }
        return columnsDataType;
    }

    public static String[] fetchColumnName(String[] columnDetails){
        String[] columnName=new String[columnDetails.length];
        for(int i=0;i<columnDetails.length;i++){
            String[] columnData=columnDetails[i].trim().split(" ");
            columnName[i]=columnData[0];
        }
        return columnName;
    }

    public int createTableFile(String dbName,String tablePath,String tableName,String[] columnDataType,String[] columnName, String query) throws IOException {
        String tableDiectoryPath=GlobalSessionDetails.getLoggedInUsername()+"/"+dbName+"/";
        File tableFile = new File(tablePath);
        int result=0;
        if(!tableFile.isFile()){
            tableFile.createNewFile();
            if(CreateStructureAndDataExportFile.insertInStructureAndDataExportFile(query,tableDiectoryPath)){
                // write logic to extract column and datatype from query
                if(SchemaDetails.createSchemaFile(tableDiectoryPath)){
                    String formattedColumnDetailsInFile=mergeColumnNameAndValue(columnName,columnDataType,"CREATE");
                    FileWriterClass.writeInFile("["+tableName.trim().toLowerCase()+"]",tableDiectoryPath+"/schemaDetails.txt");
                    SchemaDetails.insertInSchemaFile(formattedColumnDetailsInFile,tableDiectoryPath);

                }
                result=3;
            }
        }else{
            System.out.println("Table Already exists");
        }

        return result;
    }

    public String mergeColumnNameAndValue(String[] columnNames,String[] columnValues,String operationType){
        StringBuilder insertStringInFile=new StringBuilder();
        for(int i=0;i<columnNames.length;i++){
            if(i==columnNames.length-1){
                insertStringInFile.append(columnNames[i]+":"+columnValues[i]);
            }
            else{
                insertStringInFile.append(columnNames[i]+":"+columnValues[i]+"#");
            }

        }
        String formattedInsertStringInFile = insertStringInFile.toString().replaceAll(" ", "");
        formattedInsertStringInFile = formattedInsertStringInFile.replaceAll(";", "");
        return formattedInsertStringInFile;
    }



    @Override
    public int insertInTable(String query) throws IOException{
        int result=0;
        String dbName="";
        String tableName="";
        String schemaDetailPath="";

        // to spearate table and database name pattern
        Pattern tablePattern = Pattern.compile(Constants.DB_TABLE_NAME_INSERT_SEPARATOR_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matchTableResult = tablePattern.matcher(query);

        // to fetch column name from the query pattern
        Pattern ColumnValuePattern = Pattern.compile(Constants.COLUMN_NAME_VALUES_SEPARATOR_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matchColumnValueResult = ColumnValuePattern.matcher(query);

        if(matchTableResult.find()){
            // System.out.println(matchTableResult.group(1)+"Group tabel name");
            String[] separateDbtableName=matchTableResult.group(1).split("\\.");

            if(separateDbtableName.length==2){
                dbName=separateDbtableName[0];
                tableName=separateDbtableName[1].toLowerCase();

                if(DatabaseExists.validateDatabaseExistence(dbName) && TableExistence.checkIfTableExists(dbName,tableName)){
                    String insertFilePath=GlobalSessionDetails.loggedInUsername+"/"+dbName+"/"+tableName+".txt";

                    if(matchColumnValueResult.find()){
                        String[] columnNames=matchColumnValueResult.group(1).split(",");
                        String[] columnValues=matchColumnValueResult.group(2).split(",");

                        // checking if values and table column length entered by user is correct or not
                        if(columnNames.length==columnValues.length){
                            String formattedInsertStringInFile=mergeColumnNameAndValue(columnNames,columnValues,"INSERT");
                            FileWriterClass.writeInFile(formattedInsertStringInFile,insertFilePath);
                            // write validation logic before inserting into text file and match if table count is not equal to total length of total columns availabe
                            // in table then enter null for each column.
                            result=5;
                            //System.out.println(insertStringInFile+"Hello string");
                        }
                        else{
                            result=6;
                            System.out.println("Expecting "+columnNames.length+" values instead got "+columnValues.length);
                        }
                    }

                }
            }
            else{
                result=6;
                System.out.println("Please check insert syntax. Dbname not included.");
            }

        }

        return result;
    }


    // Select from table with single where condition
    //SELECT column1, column2, ...
    //FROM table_name;
    // Select * from schemaName.tableName where
    @Override
    public int fetchTableRecords(String query) throws Exception {
        int result = 0;
        String dbName = "";
        String tableName = "";
        String[] columnInQuery;
        boolean validateProvidedLegitColumns = true;

        // Logic to extract table name and database name
        Pattern patternDBTable = Pattern.compile(Constants.DB_TABLE_NAME_SELECT_SEPARATOR_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcherDBTable = patternDBTable.matcher(query);

        // Logic to extract columns
        Pattern columnsPattern = Pattern.compile("select(.*?)from", Pattern.CASE_INSENSITIVE);
        Matcher columnsMatcher = columnsPattern.matcher(query);

        if (matcherDBTable.find() && columnsMatcher.find()) {
            String[] separateDbtableName = matcherDBTable.group(0).split("\\.");

            // if query contains dbName and table name
            if (separateDbtableName.length == 2) {
                dbName = separateDbtableName[0].trim();
                tableName = separateDbtableName[1].trim().toLowerCase();
            } else if (!GlobalSessionDetails.getDbInAction().isEmpty()) {  // to ceck if user used useDB opeartion
                dbName = GlobalSessionDetails.getDbInAction().trim();
                tableName = matcherDBTable.group(0).trim().toLowerCase();
            } else {
                result = 8;
                System.out.println("Either provide dbName or use useDB operation");
            }

            if (!dbName.isEmpty() && !tableName.isEmpty()) {

                // checking database and table existence added
                if (DatabaseExists.validateDatabaseExistence(dbName) && TableExistence.checkIfTableExists(dbName, tableName)) {
                    columnInQuery = columnsMatcher.group(1).split(",");
                    List<String> totalColumn = Arrays.asList(readColumnsOfTable(dbName, tableName)); // total columns present in Table

                    if(!columnInQuery[0].trim().equals("*")){
                        validateProvidedLegitColumns=validateIfColumnIsInTable(columnInQuery,totalColumn);
                    }

                    // looping to print the fetched records
                    if (validateProvidedLegitColumns) {
                        // write a logic to read from the file
                        List<List<String>> tableRecords = readFile(dbName, tableName, columnInQuery);
                        fmt.format("\n");
                        for (int i = 0; i < tableRecords.size(); i++) {
                            for (int j = 0; j < tableRecords.get(i).size(); j++) {
                                fmt.format("%30s", tableRecords.get(i).get(j));
                            }
                            fmt.format("\n");
                        }
                        System.out.println(fmt);
                        result = 7;
                    } else {
                        result = 8;
                        System.out.println("Provided Columns does not exists in table");
                    }
                }
            }

        }
        else {
            result = 8;
            System.out.println("Please provide valid select syntax");
        }
        return result;
    }


    public boolean validateIfColumnIsInTable(String[] columnInQuery,List<String> totalColumn){
        // to check if column provided by user matches with table columns in DB
        if (columnInQuery.length > 0) {  //columnInQuery.length > 1
            for (int i = 0; i < columnInQuery.length; i++) {
                if (!totalColumn.contains(columnInQuery[i].trim())) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<List<String>> readFile(String dbName, String tableName, String[] columnsInQuery) throws Exception {
        String path = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/" + tableName + ".txt";
        List<List<String>> tableRecords = new ArrayList<List<String>>();
       // String[]columnsInQuery= Arrays.asList();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line = reader.readLine();
        int counter = 0;

        // reading table file line by lilne for select output
        while (line != null) {
            String[] splitSt = line.split("#");
            List<String> rowValue=new ArrayList<String>();
            for (int i = 0; i < splitSt.length; i++) {
                String[] columnNameValueSeparator = splitSt[i].trim().split(":");
                if(columnNameValueSeparator.length>0){

                    // if columnNames are provided or  user want to fetch all (*) records
                    if(!columnsInQuery[0].trim().equals("*")){
                       for(int ci=0;ci<columnsInQuery.length;ci++){
                            if(columnsInQuery[ci].trim().equals(columnNameValueSeparator[0])){
                                if (counter == 0) {
                                    fmt.format("%30s", columnNameValueSeparator[0]);
                                }
                                rowValue.add(columnNameValueSeparator[1]);
                            }
                       }

                    }else{
                        // if requested for all records
                        if (counter == 0) {
                            // adding here output heading which will be column name
                            fmt.format("%30s", columnNameValueSeparator[0]);
                        }
                        rowValue.add(columnNameValueSeparator[1]);
                    }
                }


            }
            tableRecords.add(rowValue);

            // setting here counter as 1 so that fmt does not contain header in a loop.
            counter = 1;
            line = reader.readLine();
        }
        reader.close();
        return tableRecords;
    }

    public String[] readColumnsOfTable(String dbName,String tableName) throws Exception {
        String tablePath = GlobalSessionDetails.getLoggedInUsername()+"/"+dbName+"/schemaDetails.txt";
        List<String> columnNamesList = new ArrayList<String>();
        boolean fetchTableName=false;
        BufferedReader reader = new BufferedReader(new FileReader(tablePath));
        String line = reader.readLine();
        int counter = 0;
        while (line != null) {
            if(fetchTableName){
                columnNamesList=Arrays.asList(line.split("#"));
                fetchTableName=false;
                break;
            }
            if(line.contains("["+tableName+"]")){
                fetchTableName=true;
            }

            line = reader.readLine();
        }
        reader.close();
        String[] columnName = new String[columnNamesList.size()];
        if(columnNamesList.size()>0){
            for(int i=0;i<columnNamesList.size();i++){
                columnName[i]=columnNamesList.get(i).split(":")[0];
            }
        }

        return columnName;
    }


    /*
    UPDATE table_name SET column1 = value1 WHERE condition;
    UPDATE university.user SET firstName = "Anita" WHERE id=1;
    */

    @Override
    public int updateATableRecords(String query) throws Exception {
        int result=0;
        String dbName="";
        String tableName="";
        String columnInQuery;
        String whereColumnName="";
        String whereColumnValue="";
        String columnNewValue="";
        String tmpFilePath="";
        String updateTablePath="";
        String oldValue="";
        boolean validateProvidedLegitColumns=false;

        // Pattern to extract schema and table name
        Pattern patternDBTable = Pattern.compile("(?<=update)(.*)(?=set)", Pattern.CASE_INSENSITIVE);
        Matcher matcherDBTable = patternDBTable.matcher(query);

        // to extract column and column new value name
        Pattern columnsPattern = Pattern.compile("(?<=set\\s).*(?=\\swhere)", Pattern.CASE_INSENSITIVE);
        Matcher columnsMatcher = columnsPattern.matcher(query);

        //to extract where condition
        Pattern wherePattern = Pattern.compile("(?<=where\\s).*", Pattern.CASE_INSENSITIVE);
        Matcher whereMatcher = wherePattern.matcher(query);

            if (matcherDBTable.find()) {
                String[] separateDbtableName = matcherDBTable.group(0).split("\\.");

                // to check if user entered bdb and table name in query. if not then check if global session contains value, if not then SOp, db absent
                if (separateDbtableName.length == 2) {
                    dbName = separateDbtableName[0].trim();
                    tableName = separateDbtableName[1].trim().toLowerCase();

                }else if(!GlobalSessionDetails.getDbInAction().isEmpty()){
                    dbName = GlobalSessionDetails.getDbInAction().trim();
                    tableName = matcherDBTable.group(0).trim().toLowerCase();
                }
                else{
                    result = 10;
                    System.out.println("Either provide dbName or use useDB operation");
                }


                if(!tableName.isEmpty() && !dbName.isEmpty()){

                    // check if db and table exists
                    if(DatabaseExists.validateDatabaseExistence(dbName) && TableExistence.checkIfTableExists(dbName, tableName)){

                            // set column value
                            if(columnsMatcher.find()){
                               // System.out.println("Grooup 0 columns"+columnsMatcher.group(0));
                                columnInQuery = columnsMatcher.group(0).trim().split("=")[0];
                                columnNewValue = columnsMatcher.group(0).trim().split("=")[1].trim();
                                //String[] validateColProvidedArray=[columnInQuery];
                                List<String> totalColumn = Arrays.asList(readColumnsOfTable(dbName, tableName)); // total columns present in Table
                                validateProvidedLegitColumns=validateIfColumnIsInTable(new String[]{columnInQuery},totalColumn);

                                if(whereMatcher.find() && validateProvidedLegitColumns){
                                    whereColumnName = whereMatcher.group(0).trim().split("=")[0];
                                    whereColumnValue = whereMatcher.group(0).trim().split("=")[1].trim();

                                    updateTablePath=GlobalSessionDetails.getLoggedInUsername()+"/"+dbName+"/"+tableName+".txt";
                                    tmpFilePath=GlobalSessionDetails.getLoggedInUsername()+"/"+dbName+"/"+tableName+"_tmp.txt";
                                    File updateFile = new File(updateTablePath);

                                    // creating a new file to write everything into it with updated value
                                    BufferedReader br = new BufferedReader(new FileReader(updateTablePath));
                                    BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFilePath));

                                    String line = br.readLine();
                                    while(line!=null){
                                        StringBuilder newLine=new StringBuilder();
                                        String[] splitSt = line.split("#");
                                        boolean filterRowFound=false;
                                        for (int i = 0; i < splitSt.length; i++) {
                                            String[] columnNameValueSeparator = splitSt[i].trim().split(":");

                                            if(columnNameValueSeparator.length>0){
                                               if(whereColumnName.trim().equals(columnNameValueSeparator[0]) && whereColumnValue.trim().equals(columnNameValueSeparator[1])){
                                                   filterRowFound=true;
                                                   oldValue=columnNameValueSeparator[1];  // loggers if want to display value changed while updating
                                               }
                                               if(filterRowFound && columnInQuery.trim().equals(columnNameValueSeparator[0])){
                                                   newLine.append(columnNameValueSeparator[0]+":"+columnNewValue+"#");
                                               }
                                               else{
                                                   newLine.append(splitSt[i]+"#");
                                               }
                                            }

                                           // System.out.println("newLine.substring(0,newLine.length()-1) "+ newLine.substring(0,newLine.length()-1));

                                            //newLine.setLength(0);
                                        }
                                        bw.append(newLine.substring(0,newLine.length()-1));
                                        bw.newLine();
                                        line = br.readLine();
                                    }

                                    bw.close();
                                    br.close();

                                    //delete the actual data file and renaming the temp file to actual file
                                    updateFile.delete();
                                    File newfile = new File(tmpFilePath);
                                    newfile.renameTo(updateFile);
                                }
                            }

                    }


                    result=9;
                }
            }
            else{
                result=10;
                System.out.println("Please enter valid update syntax");
            }
        return result;

    }


/*DELETE FROM table_name WHERE condition;
* DELETE FROM university.user WHERE id=1;
* */
    @Override
    public int deleteATableRecords(String query) throws Exception {
        int result=0;
        String dbName="";
        String tableName="";
        String whereColumnName="";
        String whereColumnValue="";
        String tmpFilePath="";
        String deleteTablePath="";
        boolean validateProvidedLegitColumns=false;

        // Pattern to extract schema and table name
        Pattern patternDBTable = Pattern.compile("(?<=from)(.*)(?=where)", Pattern.CASE_INSENSITIVE);
        Matcher matcherDBTable = patternDBTable.matcher(query);


        //to extract where condition
        Pattern wherePattern = Pattern.compile("(?<=where\\s).*", Pattern.CASE_INSENSITIVE);
        Matcher whereMatcher = wherePattern.matcher(query);

        /*if(matcherDBTable.find()){
            System.out.println("Group 0"+matcherDBTable.group(0)+"Group 1"+matcherDBTable.group(1));
            System.out.println(whereMatcher.find()+"Group 0 "+whereMatcher.group(0));
        }*/

        if (matcherDBTable.find()) {
            String[] separateDbtableName = matcherDBTable.group(0).split("\\.");

            // to check if user entered bdb and table name in query. if not then check if global session contains value, if not then SOp, db absent
            if (separateDbtableName.length == 2) {
                dbName = separateDbtableName[0].trim();
                tableName = separateDbtableName[1].trim().toLowerCase();

            }else if(!GlobalSessionDetails.getDbInAction().isEmpty()){
                dbName = GlobalSessionDetails.getDbInAction().trim();
                tableName = matcherDBTable.group(0).trim().toLowerCase();
            }
            else{
                result = 12;
                System.out.println("Either provide dbName or use useDB operation");
            }


            if(!tableName.isEmpty() && !dbName.isEmpty()){

                // check if db and table exists
                if(DatabaseExists.validateDatabaseExistence(dbName) && TableExistence.checkIfTableExists(dbName, tableName)){

                       // List<String> totalColumn = Arrays.asList(readColumnsOfTable(dbName, tableName)); // total columns present in Table
                       
                        if(whereMatcher.find()){
                            whereColumnName = whereMatcher.group(0).trim().split("=")[0];
                            whereColumnValue = whereMatcher.group(0).trim().split("=")[1].trim();

                            deleteTablePath=GlobalSessionDetails.getLoggedInUsername()+"/"+dbName+"/"+tableName+".txt";
                            tmpFilePath=GlobalSessionDetails.getLoggedInUsername()+"/"+dbName+"/"+tableName+"_tmp.txt";

                            File deleteOldTableFile = new File(deleteTablePath);

                            // creating a new file to write everything into it with deleted value
                            BufferedReader br = new BufferedReader(new FileReader(deleteTablePath));
                            BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFilePath));

                            String line = br.readLine();
                            while(line!=null){
                               // boolean containsWord=
                                if(line.contains(whereColumnName+":"+whereColumnValue)){
                                    line = br.readLine();
                                    continue;
                                }
                                else{
                                    bw.append(line);
                                    bw.newLine();
                                }
                               /* bw.append(newLine.substring(0,newLine.length()-1));
                                bw.newLine();*/
                                line = br.readLine();
                            }

                            bw.close();
                            br.close();

                            deleteOldTableFile.delete();
                            File newfile = new File(tmpFilePath);
                            newfile.renameTo(deleteOldTableFile);

                            result = 11;

                            /*filePath.delete();
                            File newfile = new File(tempFilePath);
                            newfile.renameTo(filePath);*/
                        }
                   // }

                }



            }
        }
        else{
            result=12;
            System.out.println("Please enter valid update syntax");
        }
        return result;
    }

    @Override
    public void deleteTable() {

    }

    @Override
    public void useDb() {

    }



}


/* Not Used Code
      /*Pattern tablePattern = Pattern.compile(".*create\\s+table\\s+`([a-zA-Z0-9_]*?)`.`([a-zA-Z0-9_]*?)`", Pattern.CASE_INSENSITIVE);
        Matcher matchResult = tablePattern.matcher(query);*/

//System.out.println(matchResult.find()+" file path "+matchResult.group(2)+" "+matchResult.group(1)+matchResult.group(0));
//if user enters schemaName alaongwith tabelName in the query
        /*if(separateDbtableName.length==0){
            tablePath=tableDiectoryPath.concat(analyseQuery[2])+".txt";
        }
        if(separateDbtableName.length>1){
            tablePath=tableDiectoryPath.concat(separateDbtableName[1])+".txt";
        }

         // if user enters schemaName alaongwith tabelName in the query
        /*if(separateDbtableName.length==0){
            tablePath=tableDiectoryPath.concat(analyseQuery[2])+".txt";
        }
        if(separateDbtableName.length>1){
            tablePath=tableDiectoryPath.concat(separateDbtableName[1])+".txt";
        }
*/