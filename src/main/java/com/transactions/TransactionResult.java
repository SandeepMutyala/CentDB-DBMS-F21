package com.transactions;

import utils.FileWriterClass;
import utils.GlobalSessionDetails;
import java.io.File;
import java.io.IOException;

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

	public static void commit() throws IOException {
		String directoryPath = GlobalSessionDetails.loggedInUsername + "/";
		File allDatabases = new File(directoryPath);
		for (File folder : allDatabases.listFiles()) {
			String tempFolder = GlobalSessionDetails.getLoggedInUsername() + "/" + "temp" + folder.getName();
			File tempDatabase = new File(tempFolder);
			if (tempDatabase.exists()) {
				// checking if temp folders exist
				for (File table : tempDatabase.listFiles()) {
					String permanentFilePath = GlobalSessionDetails.getLoggedInUsername() + "/"
							+ tempDatabase.getName().substring(4) + "/" + table.getName();
					File permanentTableFile = new File(permanentFilePath);
					FileWriterClass.createDuplicateCopy(permanentTableFile, table);
					table.delete();
				}
				tempDatabase.delete();
			}

		}
	}
}
