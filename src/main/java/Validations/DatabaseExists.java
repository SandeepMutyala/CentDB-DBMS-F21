package Validations;

import utils.GlobalSessionDetails;
import utils.LogPrinter;
//import utils.LogPrinter;

import java.io.File;
import java.io.IOException;

public class DatabaseExists {

    private DatabaseExists(){}

    public static boolean validateDatabaseExistence(String dbName) throws IOException {

        if(!dbName.isEmpty()){
            String dbPath = GlobalSessionDetails.loggedInUsername+"/";
            String directoryPath=dbPath.concat(dbName);

            File directory = new File(directoryPath);
            if (directory.exists()){
                return true;
            }
            else{
                System.out.println("Database doesn't exists");
                LogPrinter print = LogPrinter.getInstanceObject();
                print.errorPrinter("Database named  " + dbName + " doesn't exists.");
            }
        }
        return false;
    }
}
