package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterClass {
    public static void writeInFile(String query,String fileName){
        try (FileWriter fw = new FileWriter(fileName, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
             bw.write(query);
             bw.newLine();
             bw.flush();
             fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
