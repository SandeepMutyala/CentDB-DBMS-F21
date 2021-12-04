package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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
}
