package com.transactions;

import utils.FileWriterClass;
import utils.GlobalSessionDetails;
import java.io.File;
import java.io.IOException;

public class TransactionResult {
	public static void rollback(Boolean isRollbackAtLastIndex) {
		String directoryPath = GlobalSessionDetails.loggedInUsername + "/";
		File allFolders = new File(directoryPath);
		for (File folder : allFolders.listFiles()) {
			if (folder.getName().substring(0, 4).equals("temp")) {
				for (File file : folder.listFiles()) {
					if (!isRollbackAtLastIndex) {
						if(file.getName().equals("structureAndDataExport.txt")) {
							file.delete();
							String permanentStructureAndDataExport = GlobalSessionDetails.getLoggedInUsername()
		                            .concat("/" + folder.getName().substring(4) + "/structureAndDataExport") + ".txt";
							File permanentStructureAndDataExportFile = new File(permanentStructureAndDataExport);
							try {
								FileWriterClass.createDuplicateCopy(file, permanentStructureAndDataExportFile);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else if(!file.getName().equals("schemaDetails.txt")) {
							try {
								file.delete();
								file.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} else {
						file.delete();
					}
				}
			}
			if (isRollbackAtLastIndex) {
				folder.delete();
			}
		}
	}

	public static void commit(Boolean isCommitAtLastIndex) throws IOException {
		String directoryPath = GlobalSessionDetails.loggedInUsername + "/";
		File allDatabases = new File(directoryPath);
		for (File folder : allDatabases.listFiles()) {
			if (folder.getName().substring(0, 4).contains("temp")) {
				for (File table : folder.listFiles()) {
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
					
					  if (!isCommitAtLastIndex) { System.out.println();
					  if(!table.getName().equals("schemaDetails.txt") &&
					  !table.getName().equals("StructureAndDataExport.txt")) {
					  table.createNewFile(); } } else {
					 
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
