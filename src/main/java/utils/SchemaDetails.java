package utils;

import java.io.File;
import java.io.IOException;


import static utils.FileWriterClass.createDuplicateCopy;

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

    public static boolean insertInSchemaFile(String query, String path, Boolean isTransaction) throws IOException {
        boolean result = false;
        String completeSchemaPath = path + "/schemaDetails.txt";
        if (isTransaction) {
            String permanentExportPath = GlobalSessionDetails.loggedInUsername + "/" + GlobalSessionDetails.dbInAction.substring(4) + "/schemaDetails.txt";
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

//    public static boolean insertInSchemaFile(String query, Boolean isTransaction) throws IOException {
//        boolean result = false;
//        String completeSchemaPath = GlobalSessionDetails.loggedInUsername + "/" + GlobalSessionDetails.dbInAction + "/";
//        if (isTransaction) {
//            completeSchemaPath += "StructureAndDataExport.txt";
//            System.out.println(completeSchemaPath);
//            String permanentExportPath = GlobalSessionDetails.loggedInUsername + "/" + GlobalSessionDetails.dbInAction.substring(4) + "/StructureAndDataExport.txt";
//            File permanentExportFile = new File(permanentExportPath);
//            File tempExportFile = new File(completeSchemaPath);
//
////            if(!tempExportFile.exists()){
////                tempExportFile.createNewFile();
////            }
//            if (permanentExportFile.exists()) {
//                createDuplicateCopy(tempExportFile, permanentExportFile);
//            }
//            else {
//                try {
//                    FileWriterClass.writeInFile(query.concat(";"), completeSchemaPath);
//                    result = true;
//                } catch (Exception e) {
//                    System.out.println("An error occurred while inserting into schema");
//                    e.printStackTrace();
//                }
//            }
//        }
//        return result;
//    }



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
