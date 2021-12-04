package utils;

public class GlobalSessionDetails {

    public static String loggedInUsername="anita";
    public static String dbInAction="university";

    private GlobalSessionDetails(){}

    public static String getLoggedInUsername() {
        return loggedInUsername;
    }

    public static void setLoggedInUsername(String loggedInUsername) {
        GlobalSessionDetails.loggedInUsername = loggedInUsername;
    }

    public static String getDbInAction() {
        return dbInAction;
    }

    public static void setDbInAction(String dbInAction) {
        GlobalSessionDetails.dbInAction = dbInAction;
    }
}
