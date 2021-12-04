package utils;

public class Constants {
    public static String INVALID_SELECTION = "Please select a valid option";
    public static String USERNAME_FIELD = "Enter Username: ";
    public static String PASSWORD_FIELD = "Enter Password: ";
    public static String SECURITY_Q1_FIELD= "Set Security Question 1";
    public static String SECURITY_A1_FIELD = "Set Security Answer 1";
    public static String SECURITY_Q2_FIELD = "Set Security Question 2";
    public static String SECURITY_A2_FIELD = "Set Security Answer 2";
    public static String SECURITY_Q3_FIELD = "Set Security Question 3";
    public static String SECURITY_A3_FIELD = "Set Security Answer 3";

    public static String USER_REGISTRATION_SUCCESS_MESSAGE = "User Registered Successfully!";
    public static String USER_ALREADY_EXISTS_MESSAGE = "User already exists! You can login directly.";
    public static String USER_CREDENTIALS_NOT_FOUND = "User credentials not found! You need to register to proceed.";
    public static String USER_LOGIN_SUCCESS = "Login Success!";
    public static String USER_LOGIN_FAILED = "Login Failed!";
    public static String USER_INCORRECT_SECURITY_ANSWER_MESSAGE = "Incorrect Answer! Please contact us via email in case you have forgotten your answers!\nThanks!";
    public static String USER_POST_REGISTRATION_LOGIN_MESSAGE = "You are now logged in!\nPlease remember your credentials and Security answers to login successfully in future!";
    public static String USER_LOGIN_ANSWER_QUESTIONS_MESSAGE = "Please Answer the Security Questions set by you!";
    public static String DB_TABLE_NAME_INSERT_SEPARATOR_PATTERN=".*insert\\s+into\\s+([a-zA-Z0-9_\\.]*?)($|\\s+)";
    public static String DB_TABLE_NAME_CREATE_SEPARATOR_PATTERN=".*create\\s+table\\s+([a-zA-Z0-9_\\.]*?)\\s+(\\(.*\\))";
    public static String COLUMN_NAME_VALUES_SEPARATOR_PATTERN="\\bINSERT\\s+INTO\\s+\\S+\\s*\\(([^)]+)\\)\\s*VALUES\\s*\\(([^)]+)\\)";
    public static String DB_TABLE_NAME_SELECT_SEPARATOR_PATTERN="(?<=from)(\\s+[a-zA-Z0-9_.]+\\b)";




}
