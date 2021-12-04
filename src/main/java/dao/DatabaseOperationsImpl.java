package dao;

import Validations.DatabaseExists;
import Validations.DatatypeValidation;
import Validations.TableExistence;
import utils.*;

import java.io.File;
import java.io.IOException;
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
    * */
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
                        tableName=matchResult.group(1);
                        tablePath=GlobalSessionDetails.getLoggedInUsername().concat("/"+dbName+"/"+tableName)+".txt";

                    }
                    // if database name is in query
                    if(separateDbtableName.length>1){
                        dbName=separateDbtableName[0];
                        tableName=separateDbtableName[1];
                        tablePath=GlobalSessionDetails.getLoggedInUsername().concat("/"+dbName+"/"+tableName)+".txt";
                    }
                    if(DatabaseExists.validateDatabaseExistence(dbName)){
                        //create Table
                        result=createTableFile(dbName,tablePath,tableName,columnDataType,columnName,query);
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
                    String formattedColumnDetailsInFile=mergeColumnNameAndValue(columnName,columnDataType);
                    FileWriterClass.writeInFile("["+tableName+"]",tableDiectoryPath+"/schemaDetails.txt");
                    SchemaDetails.insertInSchemaFile(formattedColumnDetailsInFile,tableDiectoryPath);

                }
                result=3;
            }
        }else{
            System.out.println("Table Already exists");
        }

        return result;
    }

    public String mergeColumnNameAndValue(String[] columnNames,String[] columnValues){
        StringBuilder insertStringInFile=new StringBuilder();
        for(int i=0;i<columnNames.length;i++){
            insertStringInFile.append(columnNames[i]+":"+columnValues[i]+"#");
        }
        String formattedInsertStringInFile = insertStringInFile.toString().replaceAll(" ", "");
        return formattedInsertStringInFile;
    }



    @Override
    public int insertInTable(String query) throws IOException{
        int result=0;
        String dbName="";
        String tableName="";

        // to spearate table and database name pattern
        Pattern tablePattern = Pattern.compile(Constants.DB_TABLE_NAME_INSERT_SEPARATOR_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matchTableResult = tablePattern.matcher(query);

        // to fetch column name from the query pattern
        Pattern ColumnValuePattern = Pattern.compile(Constants.COLUMN_NAME_VALUES_SEPARATOR_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matchColumnValueResult = ColumnValuePattern.matcher(query);

        if(matchTableResult.find()){
            // System.out.println(matchTableResult.group(1)+"Group tabel name");
            String[] separateDbtableName=matchTableResult.group(1).split("\\.");
            dbName=separateDbtableName[0];
            tableName=separateDbtableName[1];

            if(separateDbtableName.length==2){
                if(DatabaseExists.validateDatabaseExistence(dbName) && TableExistence.checkIfTableExists(dbName,tableName)){


                    String insertFilePath=GlobalSessionDetails.loggedInUsername+"/"+dbName+"/"+tableName+".txt";

                    if(matchColumnValueResult.find()){
                        String[] columnNames=matchColumnValueResult.group(1).split(",");
                        String[] columnValues=matchColumnValueResult.group(2).split(",");

                        // checking if values and table column length entered by user is correct or not
                        if(columnNames.length==columnValues.length){
                            String formattedInsertStringInFile=mergeColumnNameAndValue(columnNames,columnValues);
                            FileWriterClass.writeInFile(formattedInsertStringInFile,insertFilePath);
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

    @Override
    public void fetchTableRecords() {

    }

    @Override
    public void updateATableRecords() {

    }

    @Override
    public void deleteATableRecords() {

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
