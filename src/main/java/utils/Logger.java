package utils;

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Logger {

    private static Logger instanceObject = null;
    private static String previouslyRecordedDate = "";
    private static boolean logDateForEvent = false;
    private static boolean logDateForGeneral = false;
    private static boolean logDateForQuery = false;

    private Logger() {
        logFilesGenerator();
    }

    public static Logger getInstanceObject() {

        if (instanceObject == null) {
            instanceObject = new Logger();
        }
        return instanceObject;
    }

    public static String generalLogFilePath = "src/main/java/com/generatedlogs/GeneralLogs.txt";
    public static String eventLogFilePath = "src/main/java/com/generatedlogs/EventLogs.txt";
    public static String queryLogFilePath = "src/main/java/com/generatedlogs/QueryLogs.txt";
    final LogPrinter printer = LogPrinter.getInstanceObject();

    FileWriter eventLog;
    FileWriter generalLog;
    FileWriter queryLog;

    private void logFilesGenerator() {

        try {
            File generalLogFile = new File(generalLogFilePath);
            File eventLogFile = new File(eventLogFilePath);
            File queryLogFile = new File(queryLogFilePath);

            generalLogFile.createNewFile();
            eventLogFile.createNewFile();
            queryLogFile.createNewFile();

            generalLog = new FileWriter(generalLogFile, true);//It will append if it's true.
            eventLog = new FileWriter(eventLogFile, true);
            queryLog = new FileWriter(queryLogFile, true);
        } catch (Exception e) {
            printer.errorPrinter(e.getMessage());
        }
    }

    private String getPresentDate() {

        SimpleDateFormat Format = new SimpleDateFormat("MMM/dd/YYYY - HH:mm:ss - EEE");
        String dateTimeDay = Format.format(new Timestamp((new Date()).getTime()));
        return "\t\n| " + dateTimeDay + " |\n";
    }

    public void sendDataToGeneralLogFile(String inputData) {
        try {
            //System.out.println("Inside write general log");
            String date = getPresentDate();
            if (date.equals(previouslyRecordedDate) && logDateForGeneral)
                date = "";
            else
                previouslyRecordedDate = date;
            previouslyRecordedDate = date;
            generalLog.write(date + "\n" + inputData + "\n\n---------------------------------------------+++++++++++++++++++++++++++---------------------------------------------\n");
            logDateForGeneral = true;
            generalLog.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDataToEventLogFile(String inputData) {
        try {
            String date = getPresentDate();
            if (date.equals(previouslyRecordedDate) && logDateForEvent)
                date = "";
            else
                previouslyRecordedDate = date;
            previouslyRecordedDate = date;
            eventLog.write(date + "\n" + inputData + "\n\n---------------------------------------------+++++++++++++++++++++++++++---------------------------------------------\n");
            logDateForEvent = true;
            eventLog.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDataToQueryLogFile(String inputData) {
        try {
            String date = getPresentDate();
            if (date.equals(previouslyRecordedDate) && logDateForQuery)
                date = "";
            else
                previouslyRecordedDate = date;
            previouslyRecordedDate = date;
            queryLog.write(date + "\n" + inputData + "\n\n---------------------------------------------+++++++++++++++++++++++++++---------------------------------------------\n");
            logDateForQuery = true;
            queryLog.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
