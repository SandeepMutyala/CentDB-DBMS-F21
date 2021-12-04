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

    public static boolean insertInSchemaFile(String query, Boolean isTransaction) throws IOException {
        boolean result = false;
        System.out.println("in 26");
        String completeSchemaPath = GlobalSessionDetails.loggedInUsername + "/" + GlobalSessionDetails.dbInAction+"/";
        System.out.println("istr "+isTransaction);
        if(isTransaction) {

            completeSchemaPath += "tempStructureAndDataExport.txt";
            System.out.println(completeSchemaPath);
            String permanentExportPath= GlobalSessionDetails.loggedInUsername + "/" + GlobalSessionDetails.dbInAction+"/StructureAndDataExport.txt";
            File permanentExportFile = new File("StructureAndDataExport.txt");
            File tempExportFile = new File("tempStructureAndDataExport.txt");
            System.out.println("line 35"+!tempExportFile.exists());
            if(!tempExportFile.exists()){
                System.out.println("in" + completeSchemaPath);
                tempExportFile.createNewFile();
            }
            if(permanentExportFile.exists()) {
                createDuplicateCopy(tempExportFile, permanentExportFile);
            }
        } else {
            completeSchemaPath += "StructureAndDataExport.txt";
        }
        try {
            System.out.println("in try"+ completeSchemaPath);
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
