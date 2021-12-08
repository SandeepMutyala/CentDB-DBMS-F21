package utils;

import java.io.*;

public class FileWriterClass {
     public static void writeInFile(String query,String filePath){
        FileWriter myWriter;
        try {
            myWriter = new FileWriter(filePath, true);
            myWriter.write(query);
            myWriter.write("\n");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void createDuplicateCopy (File fileOne, File fileTwo) throws IOException {
    	System.out.print("inside create duplicate");
        FileInputStream file2= new FileInputStream(fileTwo);
        FileOutputStream file1 = new FileOutputStream(fileOne);

        try {
            int lineIndex;
            while ((lineIndex = file2.read()) != -1) {
                file1.write(lineIndex);
            }
        }
        finally {
            if (file2 != null) {
                file2.close();
            }
            if (file1 != null) {
                file1.close();
            }
        }
    }

}
