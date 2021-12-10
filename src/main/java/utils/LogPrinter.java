package utils;

import dao.DatabaseOperationsImpl;

import java.io.*;
import java.nio.file.FileSystems;
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

    public List<Integer> generalMessagePrinter(String messageContent, String type, String dbname) throws IOException {

        String message="";
        int counter=1;
        List<Integer> counters = new ArrayList<Integer>();

        if (type.equals("table")) {
            BufferedReader br = null;
            try {
                String currentPath = (new File(".")).getCanonicalPath();
                String dbFilePath = currentPath+"\\"+GlobalSessionDetails.getLoggedInUsername() + "\\"+ dbname +"\\schemaDetails.txt";
                br = new BufferedReader(new FileReader(dbFilePath));
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
                String currentPath = (new File(".")).getCanonicalPath();
                String dbFilePath = currentPath+"\\"+GlobalSessionDetails.getLoggedInUsername() + "\\"+ dbname +"\\structureAndDataExport.txt";
                br = new BufferedReader(new FileReader(dbFilePath));
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
                String currentPath = (new File(".")).getCanonicalPath();
                String dbFilePath = currentPath+"\\"+GlobalSessionDetails.getLoggedInUsername() + "\\"+ dbname +"\\structureAndDataExport.txt";
                br = new BufferedReader(new FileReader(dbFilePath));
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
                String currentPath = (new File(".")).getCanonicalPath();
                String dbFilePath = currentPath+"\\"+GlobalSessionDetails.getLoggedInUsername() + "\\"+ dbname +"\\structureAndDataExport.txt";
                br = new BufferedReader(new FileReader(dbFilePath));
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
                String dbPath = GlobalSessionDetails.getLoggedInUsername() + "/" ;
                String exportFilePath = dbPath + "/" + "structureAndDataExport.txt";
                br = new BufferedReader(new FileReader(exportFilePath));
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
        Logger.getInstanceObject().sendDataToQueryLogFile(messageContent);
    }
}
