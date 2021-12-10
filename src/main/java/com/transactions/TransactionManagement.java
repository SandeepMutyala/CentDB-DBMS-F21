package com.transactions;

import dao.Analy;
import dao.DatabaseOperations;
import dao.DatabaseOperationsImpl;
import utils.GlobalSessionDetails;
import utils.QueryAnalyzer;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class TransactionManagement {
    static Scanner sc = new Scanner(System.in);
    public static Boolean executeQuery() {
        Integer counter = 0;
        Boolean isCommitAtLastIndex = false;
        Boolean isRollbackAtLastIndex = false;
    	DatabaseOperations dbOperations = new DatabaseOperationsImpl();
        System.out.println("Enter your Query!");
        String queryString = sc.nextLine().toLowerCase();
        try {
			Analy.query(GlobalSessionDetails.getDbInAction(), 1, GlobalSessionDetails.getLoggedInUsername());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        String filePath = "isTransactionRunning.txt";
        File f = new File(filePath);
        BufferedReader fileReader = null;
        if (isQueryFormatValid(queryString)) {
            String[] queries = queryString.split(";");
        	System.out.println(queries.length);
            if (queries.length > 1) {
                try {
                    fileReader = new BufferedReader(new FileReader(f));
                    String line = fileReader.readLine();
                    fileReader.close();
                    while(line!=null && line.contains("true")) {
                        Thread.sleep(1500);
                        fileReader = new BufferedReader(new FileReader(filePath));
                        line = fileReader.readLine();
                        fileReader.close();
                        System.out.println(line);
                    }
                    BufferedWriter fileWriter = new BufferedWriter(new FileWriter(f));
                    fileWriter.write("true");
                    fileWriter.close();
                    Thread.sleep(queries.length * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // if (Arrays.asList(queries).contains("commit") || Arrays.asList(queries).contains("rollback")) {
                    System.out.println("Transaction started!");
                    for (String query : queries) {
                    	System.out.println(query);
                        counter++;
                        query = query.trim();
                        if (query.trim().equals("rollback")) {
                            isRollbackAtLastIndex = counter == queries.length;
                                TransactionResult.rollback(isRollbackAtLastIndex);

                        }
                        if (query.trim().equals("commit")) {
                            isCommitAtLastIndex = counter == queries.length;
                            try {
                                TransactionResult.commit(isCommitAtLastIndex);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                        	QueryAnalyzer analyzer = QueryAnalyzer.getInstance();
        					queryOutputAnalysis(analyzer.splitQuery(query, dbOperations, true));
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                try {
                    BufferedWriter fileWriter = new BufferedWriter(new FileWriter(f));
                    fileWriter.write("false");
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /* } else { System.out. */
					  //println("Please enter a valid transaction! Missing RollBack or Commit."); }
					 
            } else {
                System.out.println(queryString);
				try {
					QueryAnalyzer analyzer = QueryAnalyzer.getInstance();
					queryOutputAnalysis(analyzer.splitQuery(queryString, dbOperations, false));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            return true;
        }
        System.out.println("Query Invalid");
        return false;
    }

    public static Boolean isQueryFormatValid(String query) {
        return query.trim().endsWith(";");
    }

    public static void queryOutputAnalysis(int result){
        switch(result){
            case 1: System.out.println("Db Created successfully");
                break;
            case 2: System.out.println("Database already exists");
                break;
            case 3: System.out.println("Table Created successfully");
                break;
            case 4: System.out.println("Table Creation Failed");
                break;
            case 5: System.out.println("Row inserted successfully");
                break;
            case 6: System.out.println("Insert operation Failed");
                break;
            case 7: System.out.println("Fetched records successfully");
                break;
            case 8: System.out.println("Failed to fetch records!");
                break;
            case 9: System.out.println("Updated records successfully");
                break;
            case 10: System.out.println("Failed to update records!");
                break;
            case 11: System.out.println("Deleted record successfully");
                break;
            case 12: System.out.println("Failed to delete records!");
                break;
            case 13: System.out.println("Table Dropped successfully");
                break;
            case 14: System.out.println("Failed to drop table!");
                break;
            case 15: System.out.println("Use query applied");
                break;
            case 16: System.out.println("Failed to apply use db query");
                break;
            default: System.out.println("Successful");
        }
    }
}

