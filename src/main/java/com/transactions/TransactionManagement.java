package com.transactions;

import dao.DatabaseOperations;
import dao.DatabaseOperationsImpl;
import utils.QueryAnalyzer;

import java.io.IOException;
import java.util.*;

public class TransactionManagement {
    static Scanner sc = new Scanner(System.in);
    public static Boolean executeQuery() {
    	DatabaseOperations dbOperations = new DatabaseOperationsImpl();
        System.out.println("Enter your Query!");
        String queryString = sc.nextLine().toLowerCase();
        if (isQueryFormatValid(queryString)) {
            List<String> queryList = Arrays.asList(queryString.split(";"));
            if (queryList.size() > 1) {
                System.out.println("Transaction started!");
                for (String query : queryList) {
                    query = query.trim();
                    if (query.equals("rollback")) {
                        //write the logic for rollback
                    }
                    if (query.equals("commit")) {
                        //overwrite the permanent files with the temporary files
                    }
                    try {
                        queryOutputAnalysis(QueryAnalyzer.splitQuery(query, dbOperations, true));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    System.out.println(queryString);
                    queryOutputAnalysis(QueryAnalyzer.splitQuery(queryString, dbOperations, false));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        System.out.println("Query Invalid");
        return false;
    }

    private static Boolean isQueryFormatValid(String query) {
        return query.endsWith(";");
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

