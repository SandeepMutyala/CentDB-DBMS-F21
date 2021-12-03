package utils;

import dao.genericDao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class SchemaDetails {
    public static boolean createSchemaFile(String schemaPath) {
            boolean result=false;
            String completeSchemaPath = schemaPath + "/" +  "StructureAndDataExport.txt";
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

    public static boolean insertInSchemaFile(String query) {
        boolean result=false;
        String completeSchemaPath = GlobalSessionDetails.loggedInUsername + "/" +  "StructureAndDataExport.txt";
        try {
            appendInFile(query.concat(";"),completeSchemaPath);
            result=true;
        } catch (Exception e) {
            System.out.println("An error occurred while inserting into schema");
            e.printStackTrace();
        }finally {
            return result;
        }
    }



    public static void appendInFile(String query,String fileName) {
        FileWriter myWriter;
        try {
            myWriter = new FileWriter(fileName, true);
            myWriter.write(query);
            myWriter.write("\n");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
