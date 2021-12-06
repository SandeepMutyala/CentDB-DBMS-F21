package logmanagement;

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Logger {

    private static Logger instanceObject = null;
    private static String previousDate = "";
    private static boolean eventLogDate = false;
    private static boolean generalLogDate = false;
    private static boolean queryLogDate = false;

    private Logger() {
        logFilesGenerator();
    }

    public static Logger getInstanceObject() {
        if (instanceObject == null) {
            instanceObject = new Logger();
        }
        return instanceObject;
    }

    public static String generalLogFilePath ="src/main/java/com/logs/GeneralLogs.txt";
    public static String eventLogFilePath = "src/main/java/com/logs/EventLogs.txt";
    public static String queryLogFilePath = "src/main/java/com/logs/QueryLogs.txt";
    final LogPrinter printer = LogPrinter.getInstanceObject();

    FileWriter eventLog;
    FileWriter generalLog;
    FileWriter queryLog;

    private void logFilesGenerator() {

        try {
            File generalLogFile = new File(generalLogFilePath);  //default general log text file
            File eventLogFile = new File(eventLogFilePath); //default event log text file
            File queryLogFile = new File(queryLogFilePath);  //default query log text file
            // if (generalLogFile.createNewFile())   //if no file exists, we create one
            generalLogFile.createNewFile();
//            {
//                System.out.println("Latest General Logs are updated!");
//            }
            //else if(eventLogFile.createNewFile())   //if no file exists, we create one
            eventLogFile.createNewFile();
//            {
//                System.out.println("Latest Event Logs are updated!");
//            }
            //else if(queryLogFile.createNewFile())  //if no file exists, we create one
            queryLogFile.createNewFile();
//            {
//                System.out.println("Latest Query Logs are updated!");
//            }
            generalLog = new FileWriter(generalLogFile, true);//true means appending
            eventLog = new FileWriter(eventLogFile, true);
            queryLog = new FileWriter(queryLogFile,true);
        } catch (Exception e) {
            printer.errorPrinter(e.getMessage());
        }
    }

    private String getPresentDate() {

        Date presentDate = new Date();
        Timestamp timestamp = new Timestamp(presentDate.getTime());
        SimpleDateFormat Format = new SimpleDateFormat("MMM/dd/YYYY - HH:mm:ss - EEE");
        String dateTimeDay = Format.format(timestamp);
        dateTimeDay = "\n(| "+dateTimeDay+" |)\n";
        return dateTimeDay;
    }

    public void sendDataToGeneralLogFile(String inputData) {

        try {
            String date = getPresentDate();

            if(date.equals(previousDate) && generalLogDate){
                date =  "";
            }
            else{
                previousDate = date;
            }
            generalLogDate = true;
            generalLog.write(date + "\n");
            generalLog.write(inputData + "\n");
            generalLog.write("\n---------------------------------------------+++++++++++++++++++++++++++---------------------------------------------\n");
            generalLog.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDataToEventLogFile(String inputData) {
        try {
            String date = getPresentDate();

            if(date.equals(previousDate) && eventLogDate){
                date =  "";
            }
            else{
                previousDate = date;
            }
            previousDate = date;
            eventLogDate = true;
            eventLog.write(date + "\n");
            eventLog.write(inputData+ "\n");
            eventLog.write("\n---------------------------------------------+++++++++++++++++++++++++++---------------------------------------------\n");
            eventLog.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDataToQueryLogFile(String inputData) {
        try {
            String date = getPresentDate();

            if(date.equals(previousDate) && queryLogDate){
                date =  "";
            }
            else{
                previousDate = date;
            }
            previousDate = date;
            queryLogDate = true;
            queryLog.write(date + "\n");
            queryLog.write(inputData + "\n");
            queryLog.write("\n---------------------------------------------+++++++++++++++++++++++++++---------------------------------------------\n");
            queryLog.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
