package Validations;

import utils.GlobalSessionDetails;
import utils.SchemaDetails;

import java.io.File;
import java.io.IOException;

public class DatabaseExists {

    private DatabaseExists(){}

    public static boolean validateDatabaseExistence(String dbName) throws IOException {

        if(!dbName.isEmpty()){
            String dbPath = GlobalSessionDetails.loggedInUsername+"/";
            String directoryPath=dbPath.concat(dbName)+"/";
            System.out.println(directoryPath);
            File directory = new File(directoryPath);
            if (directory.exists()){
            	System.out.println("directory exists");
                return true;
            }
        }else{
            System.out.println("Database doesn't exists");
        }
        return false;
    }
}
