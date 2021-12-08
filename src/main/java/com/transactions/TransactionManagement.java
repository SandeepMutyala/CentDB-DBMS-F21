package com.transactions;

import dao.DatabaseOperations;
import dao.DatabaseOperationsImpl;
import utils.QueryAnalyzer;

import java.io.IOException;
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
        if (isQueryFormatValid(queryString)) {
        	String[] queries = queryString.split(";");
        	System.out.println(queries.length);
            if (queries.length > 1) {
                if (Arrays.asList(queries).contains("commit") || Arrays.asList(queries).contains("rollback")) {
                    System.out.println("Transaction started!");
                    for (String query : queries) {
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
                            queryOutputAnalysis(QueryAnalyzer.splitQuery(query, dbOperations, true));
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else {
                    System.out.println("Please enter a valid transaction! Missing RollBack or Commit.");
                }
            } else {
                System.out.println(queryString);
				try {
					queryOutputAnalysis(QueryAnalyzer.splitQuery(queryString, dbOperations, false));
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

    private static Boolean isQueryFormatValid(String query) {
        return query.trim().endsWith(";");
    }

    public static void queryOutputAnalysis(int result) {
        switch (result) {
            case 1:
                System.out.println("Db Created successfully");
                break;
            case 2:
                System.out.println("Database already exists");
                break;
            case 3:
                System.out.println("Table Created successfully");
                break;
            case 4:
                System.out.println("Table Creation Failed");
                break;
            default:
                System.out.println("Oops! Creation failed");
        }
    }
}

