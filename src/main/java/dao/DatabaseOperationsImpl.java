package dao;

import Validations.DatabaseExists;
import Validations.DatatypeValidation;
import Validations.TableExistence;
import utils.Constants;
import utils.FileWriterClass;
import utils.GlobalSessionDetails;
import utils.SchemaDetails;
import utils.CreateStructureAndDataExportFile;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.FileWriterClass.createDuplicateCopy;

public class DatabaseOperationsImpl implements DatabaseOperations {
    /*
    * result=1, db successfully created
    * result=2, db already exists
    * result=3, Table created successfully
    * result=4, Table creation failed
    * */
    @Override
    public  int createDb(String query, Boolean isTransaction) throws IOException {
        int result=0;
        String[] analyseQuery=query.split(" ");

        String dbPath = isTransaction ? GlobalSessionDetails.loggedInUsername+"/temp": GlobalSessionDetails.loggedInUsername+"/";
        String directoryPath=dbPath.concat(analyseQuery[2]);
        GlobalSessionDetails.dbInAction = isTransaction? "temp"+analyseQuery[2] : analyseQuery[2];
        /*if(analyseQuery[2].substring(analyseQuery[2].length() - 1).equals(";")){
            directoryPath = dbPath.concat(analyseQuery[2].substring(0,analyseQuery[2].length()-1));
        }
        else{
            directoryPath=dbPath.concat(analyseQuery[2]);
        }*/
        // String fileName = id + getTimeStamp() + ".txt";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
            if (isTransaction) {
                String permanantDirectoryPath = GlobalSessionDetails.loggedInUsername+ "/".concat(analyseQuery[2]);
                File permanantDirectory = new File(permanantDirectoryPath);
                if (permanantDirectory.exists()) {
                	System.out.println("The requested Database already exists");
                    //createDuplicateCopy(directory, permanantDirectory);
                }else if(!directory.exists()){
                	directory.createNewFile();
                }else {
                	System.out.println("Database created");
                }
            }
            
            if (SchemaDetails.insertInSchemaFile(query, isTransaction)) {
                result = 1;
            }
        }
        return 2;
    }

    @Override
    public int createTable(String query, Boolean isTransaction) {
        int result=0;
        //String tableDiectoryPath = GlobalSessionDetails.loggedInUsername + "/";
        String tablePath="";
        String tempTablePath = "";
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
                System.out.println(columnDataType[0]);
                columnName=fetchColumnName(removedPrimaryKeyColumnDetails);


                if(DatatypeValidation.validateTableDataType(columnDataType)){
                    // if database name is not in the query
                    if(separateDbtableName.length<=1){
                        dbName=GlobalSessionDetails.getDbInAction();
                        tableName=matchResult.group(1);
                        tempTablePath = GlobalSessionDetails.getLoggedInUsername().concat("/"+dbName+"/"+tableName)+".txt";
                        tablePath=GlobalSessionDetails.getLoggedInUsername().concat("/"+dbName+"/"+tableName)+".txt";

                    }
                    // if database name is in query
                    if(separateDbtableName.length>1){
                        dbName=separateDbtableName[0];
                        tableName=separateDbtableName[1];
                        tempTablePath = GlobalSessionDetails.getLoggedInUsername().concat("/"+dbName+"/"+tableName)+".txt";
                        tablePath=GlobalSessionDetails.getLoggedInUsername().concat("/"+dbName+"/"+tableName)+".txt";
                    }
                    
                    
                    if(isTransaction) {
                    	if(DatabaseExists.validateDatabaseExistence(dbName)){
                            //create Temporary Table
                    		String permanentTablePath = GlobalSessionDetails.getLoggedInUsername().concat("/"+dbName.substring(4)+"/"+tableName)+".txt";
                    		File permanentTable = new File(permanentTablePath);
                    		if(permanentTable.exists()) {
                    			File tempTable = new File(tempTablePath);
                    			FileWriterClass.createDuplicateCopy(tempTable, permanentTable);
                    		}else {
                            result=createTableFile(dbName,tempTablePath,tableName,columnDataType,columnName,query,isTransaction);
                    		}
                        }
                    	else {
                    		System.out.println("Please create or choose a Database first");
                    	}
                    }else {
                    	if(DatabaseExists.validateDatabaseExistence(dbName)){
                            //create Table
                            result=createTableFile(dbName,tablePath,tableName,columnDataType,columnName,query,isTransaction);
                        }
                    	else {
                    		System.out.println("Please create or choose a Database first");
                    	}
                    }
                    
                }
                else{
                    System.out.println("Please select valid datatype");
                }
            }
            else{
                System.out.println("Please check the syntax for table creation");
            }
        }
        catch(Exception ex){
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

    public int createTableFile(String dbName,String tablePath,String tableName,String[] columnDataType,String[] columnName, String query,Boolean isTransaction) throws IOException {
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
                    SchemaDetails.insertInSchemaFile(formattedColumnDetailsInFile,isTransaction);

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
    public int insertInTable(String query, Boolean isTranction) throws IOException{
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
            // System.out.println(matchTableResult.group(1)+"Group table name");
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
		// TODO Auto-generated method stub
		
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
