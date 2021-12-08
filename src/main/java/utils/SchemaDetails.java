package utils;

import java.io.File;
import java.io.IOException;


import static utils.FileWriterClass.createDuplicateCopy;

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

    public static boolean insertInSchemaFile(String query, String dbName, Boolean isTransaction) throws IOException {
        boolean result = false;
        String completeSchemaPath=GlobalSessionDetails.getLoggedInUsername()+"/"+dbName+"/schemaDetails.txt";
        if (isTransaction) {
            String permanentExportPath = GlobalSessionDetails.loggedInUsername + "/" + dbName.substring(4) + "/schemaDetails.txt";
            File permanentExportFile = new File(permanentExportPath);
            File tempExportFile = new File(completeSchemaPath);
            if (permanentExportFile.exists()) {
                createDuplicateCopy(tempExportFile, permanentExportFile);
            } else {
                try {
                    FileWriterClass.writeInFile(query.concat(";"), completeSchemaPath);
                    result = true;
                } catch (Exception e) {
                    System.out.println("An error occurred while inserting into schema");
                    e.printStackTrace();
                }
            }
        } else {
            try {
                FileWriterClass.writeInFile(query.concat(";"), completeSchemaPath);
                result = true;
            } catch (Exception e) {
                System.out.println("An error occurred while inserting into schema");
                e.printStackTrace();
            }
        }
        return result;
    }




}
