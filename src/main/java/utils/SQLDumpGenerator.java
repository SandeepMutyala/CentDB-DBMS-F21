package utils;

import Validations.DatabaseExists;

import java.io.*;

public class SQLDumpGenerator {
    public static void dumpGenerator(String dbName) throws IOException {

        if (!dbName.isEmpty() && DatabaseExists.validateDatabaseExistence(dbName)) {

            String dbPath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName;
            String exportFilePath = dbPath + "/" + "structureAndDataExport.txt";

            String exportSystemFilePath = "exportFile.sql";
            // creating a new file to write everything into it
            BufferedReader br = new BufferedReader(new FileReader(exportFilePath));
            BufferedWriter bw = new BufferedWriter(new FileWriter(exportSystemFilePath));

            String line = br.readLine();
            while (line != null) {

                bw.append(line);
                bw.newLine();
                line = br.readLine();
            }

            bw.close();
            br.close();
        }
    }
}
