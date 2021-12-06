package logmanagement;

import static java.lang.Thread.sleep;

public class LogPrinter {

    private static LogPrinter instanceObject = null;

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
        //System.out.print("\n" + messageContent +"\n");
        Logger.getInstanceObject().sendDataToGeneralLogFile(messageContent);
        // Logger.getInstanceObject().sendDataToEventLogFile(messageContent);
    }

    public void queryMessagePrinter(String messageContent) {
        Logger.getInstanceObject().sendDataToQueryLogFile(messageContent);
    }

    //Unused code
//    public void errorInCommand(String error) {
//        errorInCommand("\nPlease enter correct command");
//        Logger.getInstanceObject().sendDataToEventLogFile("\n\tError: Please enter correct command\n");
//    }
}
