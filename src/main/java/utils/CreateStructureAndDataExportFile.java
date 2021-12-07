package utils;

import dao.genericDao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class CreateStructureAndDataExportFile {
    public static boolean structureAndDataExportFileCreation(String dbName) {
            boolean result=false;
            String completeSchemaPath = GlobalSessionDetails.getLoggedInUsername()+"/"+dbName + "/" +  "structureAndDataExport.txt";
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

    public static boolean insertInStructureAndDataExportFile(String query,String dbName) {
        boolean result=false;
        String completeSchemaPath=GlobalSessionDetails.getLoggedInUsername()+"/"+dbName+"/structureAndDataExport.txt";
        /*if(GlobalSessionDetails.getDbInAction().isEmpty()){
            completeSchemaPath = GlobalSessionDetails.loggedInUsername + "/" + GlobalSessionDetails.dbInAction+"/"+ "structureAndDataExport.txt";
        }*/
       // String completeSchemaPath = GlobalSessionDetails.loggedInUsername + "/" + GlobalSessionDetails.dbInAction+"/"+ "structureAndDataExport.txt";
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



    /*public static void appendInFile(String query,String fileName) {
        FileWriter myWriter;
        try {
            myWriter = new FileWriter(fileName, true);
            myWriter.write(query);
            myWriter.write("\n");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

}
