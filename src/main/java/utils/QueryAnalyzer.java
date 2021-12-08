package utils;

import dao.DatabaseOperations;

import java.io.IOException;

public class QueryAnalyzer {

	// public DatabaseOperations dbOperations;
	private static QueryAnalyzer ANALYZER;

	private QueryAnalyzer() {

	}

	public static QueryAnalyzer getInstance() {
		if (ANALYZER == null) {
			ANALYZER = new QueryAnalyzer();
		}
		return ANALYZER;
	}

	public int splitQuery(String query, DatabaseOperations dbOperations, Boolean isTransaction)
			throws Exception {
		String formattedQuery = isTransaction ? query : removeSemiColon(query);
		String[] analyseQuery = formattedQuery.split(" ");
		int output = 0;
		// for(int i=0;i<analyseQuery.length;i++){
		System.out.println("formattedQ  " + formattedQuery);
		switch (analyseQuery[0].toUpperCase()) {
		case "CREATE":
			if (analyseQuery[1].equalsIgnoreCase("DATABASE") || analyseQuery[1].equalsIgnoreCase("SCHEMA")) {
				output = dbOperations.createDb(formattedQuery, isTransaction);

			}
			if (analyseQuery[1].equalsIgnoreCase("TABLE")) {
				System.out.println("in");
				output = dbOperations.createTable(formattedQuery, isTransaction);
			}
			break;
		case "INSERT":
			dbOperations.insertInTable(formattedQuery, isTransaction);
			break;
		case "SELECT":
			output = dbOperations.fetchTableRecords(formattedQuery, isTransaction);
			break;
		case "UPDATE":
			output = dbOperations.updateATableRecords(formattedQuery, isTransaction);
			break;
		case "DELETE":
			output = dbOperations.deleteATableRecords(formattedQuery, isTransaction);
			break;
		case "DROP":
			output = dbOperations.deleteTable(formattedQuery, isTransaction);
			break;

		case "USE":
			output = dbOperations.useDb(formattedQuery, isTransaction);
			break;
		default:
		}
		/*
		 * if(analyseQuery[i].equalsIgnoreCase("create")){
		 * 
		 * } else if(analyseQuery[i].equalsIgnoreCase("insert")){
		 * 
		 * } else if(analyseQuery[i].equalsIgnoreCase("update")){
		 * 
		 * } else if(analyseQuery[i].equalsIgnoreCase("delete")){
		 * 
		 * } else{
		 * 
		 * }
		 */
		// }
		return output;
	}

	public static String removeSemiColon(String inputString) {
		String outputString = "";
		if (inputString != null && !inputString.isEmpty() && inputString.charAt(inputString.length() - 1) == ';') {
			outputString = inputString.substring(0, inputString.length() - 1);
		} else {
			outputString = inputString;
		}
		return outputString.trim();
	}
}
