package logmanagement;

import static java.lang.Thread.sleep;

public class LogPrinter {

    private static LogPrinter instanceObject = null;

    private LogPrinter() {
        // required private constructor
    }

    public static LogPrinter getInstanceObject() {
        if (instanceObject == null) {
            instanceObject = new LogPrinter();
        }
        return instanceObject;
    }

    public void errorPrinter(String errorMessage) {
        System.err.print("\n" + errorMessage +"\n");
        Logger.getInstanceObject().sendDataToEventLogFile("Error: " + errorMessage);
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void generalMessagePrinter(String messageContent) {
        System.out.print("\n" + messageContent +"\n");
        Logger.getInstanceObject().sendDataToGeneralLogFile(messageContent);
       // Logger.getInstanceObject().sendDataToEventLogFile(messageContent);
    }

    public void queryMessagePrinter(String messageContent) {
        System.out.print("\n" + messageContent +"\n");
        Logger.getInstanceObject().sendDataToQueryLogFile(messageContent);
    }

    public void createLogFiles() {

    }

    public void errorInCommand() {
        errorPrinter("\n\tPlease enter relevant command\n");
        Logger.getInstanceObject().sendDataToEventLogFile("\n\tError: Please enter relevant command\n");
    }
}
