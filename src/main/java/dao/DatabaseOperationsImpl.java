package dao;

import utils.GlobalSessionDetails;
import utils.SchemaDetails;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseOperationsImpl implements DatabaseOperations {

    /*
    * result=1, db successfully created
    * result=2, db already exists
    * result=3, Table created successfully
    * result=4, Table creation failed
    * */
    @Override
    public  int createDb(String query){
        int result=0;
        String[] analyseQuery=query.split(" ");
        String dbPath = GlobalSessionDetails.loggedInUsername+"/";
        String directoryPath=dbPath.concat(analyseQuery[2]);
        /*if(analyseQuery[2].substring(analyseQuery[2].length() - 1).equals(";")){
            directoryPath = dbPath.concat(analyseQuery[2].substring(0,analyseQuery[2].length()-1));
        }
        else{
            directoryPath=dbPath.concat(analyseQuery[2]);
        }*/
        // String fileName = id + getTimeStamp() + ".txt";

        File directory = new File(directoryPath);
        if (!directory.exists()){
            directory.mkdirs();
            if(SchemaDetails.insertInSchemaFile(query)){
                result=1;
            }
        }
        return 2;
    }

    @Override
    public int createTable(String query) {
        int result=0;
        String tableDiectoryPath = GlobalSessionDetails.loggedInUsername + "/" + GlobalSessionDetails.dbInAction+"/";
        String tablePath="";
        Pattern tablePattern = Pattern.compile(".*create\\s+table\\s+([a-zA-Z0-9_\\.]*?)($|\\s+)", Pattern.CASE_INSENSITIVE);
        Matcher matchResult = tablePattern.matcher(query);
        try{
            if (matchResult.find()) {
               // System.out.println("----"+"kkk "+matchResult.group(1)+"fsdfd "+matchResult.group(2));
                String[] separateDbtableName=matchResult.group(1).split("\\.");
                if(separateDbtableName.length<=1){
                    tablePath=tableDiectoryPath.concat(matchResult.group(1))+".txt";
                }
                if(separateDbtableName.length>1){
                    tablePath=tableDiectoryPath.concat(separateDbtableName[1])+".txt";
                }
                File tableFile = new File(tablePath);
                    tableFile.createNewFile();
                    if(SchemaDetails.insertInSchemaFile(query)){
                        result=3;
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



    @Override
    public void useDb() {

    }

    /*INSERT INTO schema.table_name (column1, column2, column3, ...)
VALUES (value1, value2, value3, ...);*/
    @Override
    public void insertInTable(String query) {

        Pattern tablePattern = Pattern.compile(".*insert\\s+into\\s+([a-zA-Z0-9_\\.]*?)([(a-zA-Z0-9_\\.]*?)($|\\s+)", Pattern.CASE_INSENSITIVE);
        Pattern ColumnValuePattern = Pattern.compile("\\bINSERT\\s+INTO\\s+\\S+\\s*\\(([^)]+)\\)\\s*VALUES\\s*\\(([^)]+)\\)", Pattern.CASE_INSENSITIVE);
        Matcher matchColumnValueResult = ColumnValuePattern.matcher(query);

        System.out.println("insert query"+matchColumnValueResult.find());
        System.out.println("----"+"0=> "+matchColumnValueResult.group(0)+"1=> "+matchColumnValueResult.group(1)+"2->"+matchColumnValueResult.group(2));

        // Check if Database exists
        // Check if Tabel exists
        // Map column name with values , the order it si coming in and then store in txt file
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
