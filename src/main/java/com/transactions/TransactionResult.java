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

	public static void commit(Boolean isCommitAtLastIndex) throws IOException {
		System.out.println("lastcom"+isCommitAtLastIndex);
		String directoryPath = GlobalSessionDetails.loggedInUsername + "/";
		File allDatabases = new File(directoryPath);
		for (File folder : allDatabases.listFiles()) {
			System.out.println("Folder"+folder.getName());
			if (folder.getName().substring(0, 4).contains("temp")) {
				for (File table : folder.listFiles()) {
					System.out.println("in forloop");
					String permanentFilePath = GlobalSessionDetails.getLoggedInUsername() + "/"
							+ folder.getName().substring(4) + "/" + table.getName();
					String directorypath = GlobalSessionDetails.getLoggedInUsername() + "/"
							+ folder.getName().substring(4) + "/";
					File directory = new File(directorypath);
					File permanentTableFile = new File(permanentFilePath);
					if (directory.exists()) {
						permanentTableFile.createNewFile();
					} else {
						directory.mkdirs();
						permanentTableFile.createNewFile();
					}
					FileWriterClass.createDuplicateCopy(permanentTableFile, table);
					System.out.println(table.getName());
					if (!isCommitAtLastIndex) {
						System.out.println();
						if(!table.getName().equals("schemaDetails.txt") &&
							!table.getName().equals("StructureAndDataExport.txt")) {
							table.createNewFile();
						}
					} else {
						table.delete();
					}
				}
			}
			if (isCommitAtLastIndex) {
				folder.delete();
			}
		}
	}
}
