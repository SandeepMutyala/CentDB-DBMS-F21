package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class LogPrinter {

    private static LogPrinter instanceObject = null;

    public static LogPrinter getInstanceObject() {
        if (instanceObject == null) {
            instanceObject = new LogPrinter();
        }
        return instanceObject;
    }

    public List<Integer> generalMessagePrinter(String messageContent, String type) throws IOException {

        String message="";
        int counter=1;
        List<Integer> counters = new ArrayList<Integer>();

        if (type.equals("table")) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader("5dbc98dcc983a70728bd082d1a47546e/Store/schemaDetails.txt"));
                String data = null;
                while ((data = br.readLine()) != null) {
                    if (data.matches("\\[{1}\\w*\\]")) {
                        counter++;
                    }
                }
                message=messageContent+counter+" tables";
            } catch (Exception e) {
                message="failed to create table";
                e.printStackTrace();
            } finally {
                Logger.getInstanceObject().sendDataToGeneralLogFile(message);
                //counters.add(tableCounter);
                br.close();
            }
        }

        if (type.equals("insert")) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader("5dbc98dcc983a70728bd082d1a47546e/Store/StructureAndDataExport.txt"));
                String data = null;
                while ((data = br.readLine()) != null) {
                    if (data.toLowerCase().contains("insert into")) {
                        counter++;
                    }
                }
                message=messageContent+counter+" insert statements";
            } catch (IOException e) {
                message="failed to insert into table";
                e.printStackTrace();
            } finally {
                Logger.getInstanceObject().sendDataToGeneralLogFile(message);
                br.close();
            }
        }

        if (type.equals("update")) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader("5dbc98dcc983a70728bd082d1a47546e/Store/StructureAndDataExport.txt"));
                String data = null;
                while ((data = br.readLine()) != null) {
                    if (data.toLowerCase().contains("update")) {
                        counter++;
                    }
                }
                message=messageContent+counter+" update statements";
            } catch (IOException e) {
                message="failed to update into table";
                e.printStackTrace();
            } finally {
                Logger.getInstanceObject().sendDataToGeneralLogFile(message);
                br.close();
            }
        }

        if (type.equals("delete")) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader("5dbc98dcc983a70728bd082d1a47546e/Store/StructureAndDataExport.txt"));
                String data = null;
                while ((data = br.readLine()) != null) {
                    if (data.toLowerCase().contains("delete")) {
                        counter++;
                    }
                }
                message=messageContent+counter+" delete statements";
            } catch (IOException e) {
                message="failed to delete record";
                e.printStackTrace();
            } finally {
                Logger.getInstanceObject().sendDataToGeneralLogFile(message);
                br.close();
            }
        }

        if (type.equals("drop")) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader("5dbc98dcc983a70728bd082d1a47546e/company/StructureAndDataExport.txt"));
                String data = null;
                while ((data = br.readLine()) != null) {
                    if (data.toLowerCase().contains("drop")) {
                        counter++;
                    }
                }
                message=messageContent+counter+" drop statements";
            } catch (IOException e) {
                message="failed to drop table";
                e.printStackTrace();
            } finally {
                Logger.getInstanceObject().sendDataToGeneralLogFile(message);
                br.close();
            }
        }
        return counters;
    }


//        int tableCounter = 0;
//        BufferedReader br = null;
//        try {
//            br = new BufferedReader(new FileReader("5dbc98dcc983a70728bd082d1a47546e/Store/schemaDetails.txt"));
//            String data = null;
//            while((data = br.readLine())!=null){
//                if(data.matches("\\[{1}\\w*\\]")){
//                    tableCounter++;
//                }
//            }
//            int insertCounter =0;
//            br = new BufferedReader(new FileReader("5dbc98dcc983a70728bd082d1a47546e/Store/StructureAndDataExport.txt"));
//            String insertData = null;
//            while((insertData = br.readLine())!=null) {
//                //if (Data.matches(Constants.DB_TABLE_NAME_INSERT_SEPARATOR_PATTERN)) {
//                if (insertData.toLowerCase().contains("insert into")) {
//                    insertCounter++;
//                }
//                }
//            Logger.getInstanceObject().sendDataToGeneralLogFile(messageContent,count);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally{
//            br.close();
//        }
//        counters.add(insertcounter);
//    }


    public void errorPrinter(String errorMessage) {

        Logger.getInstanceObject().sendDataToEventLogFile("Error: " + errorMessage);
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void queryMessagePrinter(String messageContent) {
        //System.out.println("Write to query log");
        Logger.getInstanceObject().sendDataToQueryLogFile(messageContent);
    }

    //Unused code
//    public void errorInCommand(String error) {
//        errorInCommand("\nPlease enter correct command");
//        Logger.getInstanceObject().sendDataToEventLogFile("\n\tError: Please enter correct command\n");
//    }
}
