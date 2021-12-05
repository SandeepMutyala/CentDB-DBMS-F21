package Validations;

import utils.GlobalSessionDetails;

import java.io.File;

public class TableExistence {

    private TableExistence(){}

    public static boolean checkIfTableExists(String dbName,String tableName){

        if (!tableName.isEmpty()){
            String tablePath = GlobalSessionDetails.loggedInUsername+"/"+dbName + "/" + tableName + ".txt";
            File filePath = new File(tablePath);

            Boolean fileExist = filePath.isFile();
            if(fileExist){
                return true;
            }
        }

        System.out.println("Table does not exists");
        return false;
    }
}
