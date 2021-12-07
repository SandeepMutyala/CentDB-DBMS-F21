package utils;

import java.io.File;

public class SchemaDetails {
    public static boolean createSchemaFile(String dbName) {
        boolean result=false;
        String completeSchemaPath = GlobalSessionDetails.getLoggedInUsername()+"/"+dbName + "/" +  "schemaDetails.txt";
        File schemaFile = new File(completeSchemaPath);
        try{
            schemaFile.createNewFile();
            result=true;
        }catch(Exception ex){
            System.out.println("Unable to create Database Schema");
        }finally {
            return result;
        }
    }

    public static boolean insertInSchemaFile(String query,String dbName) {
        boolean result=false;
        String completeSchemaPath=GlobalSessionDetails.getLoggedInUsername()+"/"+dbName+"/schemaDetails.txt";

        try {
            FileWriterClass.writeInFile(query.concat(";"),completeSchemaPath);
            result=true;
        } catch (Exception e) {
            System.out.println("An error occurred while inserting into schema");
            e.printStackTrace();
        }finally {
            return result;
        }
    }
}
