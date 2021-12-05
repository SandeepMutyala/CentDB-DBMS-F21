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
        String query = null;
        query = sc.nextLine().toLowerCase();
        if (query.contains("start transaction")) {
            System.out.println("Transaction started!");
            while (query != "commit") {
                System.out.println("Please enter a query!");
                query = sc.nextLine().toLowerCase();
                if(query == "rollback") {
                	//write the logic for rollback
                }
                if(query == "commit") {
                	//overwrite the permanent files with the temporary files
                }
                
                try {
                	System.out.println(query);
                    queryOutputAnalysis(QueryAnalyzer.splitQuery(query, dbOperations, true));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
        	try {
        		System.out.println(query);
                queryOutputAnalysis(QueryAnalyzer.splitQuery(query, dbOperations, false));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
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

