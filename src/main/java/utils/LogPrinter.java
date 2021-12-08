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
                br = new BufferedReader(new FileReader("21b90964dd770544d14ee67b261b4adb/dal/schemaDetails.txt"));
                String data = null;
                while ((data = br.readLine()) != null) {
                    if (data.matches("\\[{1}\\w*\\]")) {
                        counter++;
                    }
                }
                message = messageContent + counter + " tables";
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
                br = new BufferedReader(new FileReader("21b90964dd770544d14ee67b261b4adb/dal/StructureAndDataExport.txt"));
                String data = null;
                while ((data = br.readLine()) != null) {
                    if (data.toLowerCase().contains("insert into")) {
                        counter++;
                    }
                }
                message = messageContent + counter + " insert statements";
            } catch (IOException e) {
                message = "failed to insert into table";
                e.printStackTrace();
            } finally {
                Logger.getInstanceObject().sendDataToGeneralLogFile(message);
                br.close();
            }
        }

        if (type.equals("update")) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader("21b90964dd770544d14ee67b261b4adb/dal/StructureAndDataExport.txt"));
                String data = null;
                while ((data = br.readLine()) != null) {
                    if (data.toLowerCase().contains("update")) {
                        counter++;
                    }
                }
                message = messageContent + counter + " update statements";
            } catch (IOException e) {
                message = "failed to update into table";
                e.printStackTrace();
            } finally {
                Logger.getInstanceObject().sendDataToGeneralLogFile(message);
                br.close();
            }
        }

        if (type.equals("delete")) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader("21b90964dd770544d14ee67b261b4adb/dal/StructureAndDataExport.txt"));
                String data = null;
                while ((data = br.readLine()) != null) {
                    if (data.toLowerCase().contains("delete")) {
                        counter++;
                    }
                }
                message = messageContent + counter + " delete statements";
            } catch (IOException e) {
                message = "failed to delete record";
                e.printStackTrace();
            } finally {
                Logger.getInstanceObject().sendDataToGeneralLogFile(message);
                br.close();
            }
        }

        if (type.equals("drop")) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader("21b90964dd770544d14ee67b261b4adb/dal/StructureAndDataExport.txt"));
                String data = null;
                while ((data = br.readLine()) != null) {
                    if (data.toLowerCase().contains("drop")) {
                        counter++;
                    }
                }
                message = messageContent + counter + " drop statements";
            } catch (IOException e) {
                message = "failed to drop table";
                e.printStackTrace();
            } finally {
                Logger.getInstanceObject().sendDataToGeneralLogFile(message);
                br.close();
            }
        }
        return counters;
    }

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
}
