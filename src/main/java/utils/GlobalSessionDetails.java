package utils;

import java.util.HashMap;
import java.util.Map;

public class GlobalSessionDetails {

    public static String loggedInUsername;
    public static String dbInAction;
   public static Map<String,String> tableRecords=new HashMap<String, String>();
    private GlobalSessionDetails(){}

    public static String getLoggedInUsername() {
        return loggedInUsername;
    }

    public static void setLoggedInUsername(String loggedInUsername) {
        GlobalSessionDetails.loggedInUsername = loggedInUsername;
    }

    public static Map<String, String> getTableRecords() {
        return tableRecords;
    }

    public static void setTableRecords(Map<String, String> tableRecords) {
        GlobalSessionDetails.tableRecords = tableRecords;
    }

    public static String getDbInAction() {
        return dbInAction;
    }

    public static void setDbInAction(String dbInAction) {
        GlobalSessionDetails.dbInAction = dbInAction;
    }
}
