package com.transactions;

import utils.GlobalSessionDetails;
import java.io.File;

public class TransactionResult {
    public static void rollback() {
        String directoryPath = GlobalSessionDetails.loggedInUsername + "/";
        File allFolders = new File(directoryPath);
        for (File folder : allFolders.listFiles()) {
            System.out.println(folder.getName().substring(0, 4));
            if (folder.getName().substring(0, 4).equals("temp")) {
                for (File file : folder.listFiles()) {
                    file.delete();
                }
            }
            folder.delete();
        }
    }

    public static void commit() {
        String directoryPath = GlobalSessionDetails.loggedInUsername + "/";
        String tempDirectoryPath = GlobalSessionDetails.loggedInUsername + "/" +;
        String permanantDirectoryPath = GlobalSessionDetails.loggedInUsername + "/" + GlobalSessionDetails.dbInAction.substring(4);
        String tempFilePath;
        String permanentFilePath;
        File allTempTables = new File(tempDirectoryPath);
        for (File file : allTempTables.listFiles()) {
            tempFilePath = tempDirectoryPath.concat("/"+ file.getName());
            permanentFilePath = permanantDirectoryPath.concat("/"+ file.getName());



        }
    }
}

