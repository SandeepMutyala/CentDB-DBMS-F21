package utils;

import dao.DatabaseOperations;

import java.io.IOException;

public class QueryAnalyzer {

    // public DatabaseOperations dbOperations;

    /*QueryAnalyzer(DatabaseOperations dbOperations){
        this.dbOperations=dbOperations;
    }*/

    public  static int splitQuery(String query,DatabaseOperations dbOperations) throws Exception {
        String formattedQuery=removeSemiColon(query);
        String[] analyseQuery=formattedQuery.split(" ");
        int output=0;
        // for(int i=0;i<analyseQuery.length;i++){
        switch(analyseQuery[0].toUpperCase()){
            case "CREATE":
                if(analyseQuery[1].equalsIgnoreCase("DATABASE") || analyseQuery[1].equalsIgnoreCase("SCHEMA")){
                    output=dbOperations.createDb(formattedQuery);

                }
                if(analyseQuery[1].equalsIgnoreCase("TABLE")){
                    output=dbOperations.createTable(formattedQuery);
                }
                break;
            case "INSERT":
                output=dbOperations.insertInTable(formattedQuery);
                break;
            case "SELECT":
                output=dbOperations.fetchTableRecords(formattedQuery);
                break;
            case "UPDATE":
                output=dbOperations.updateATableRecords(formattedQuery);
                break;
            case "DELETE":
                output=dbOperations.deleteATableRecords(formattedQuery);
                break;
            case "DROP":
                output=dbOperations.deleteTable(formattedQuery);
                break;
            case "USE":
                output=dbOperations.useDb(formattedQuery);
                break;
            default:
        }
            /*if(analyseQuery[i].equalsIgnoreCase("create")){

            }
            else if(analyseQuery[i].equalsIgnoreCase("insert")){

            }
            else if(analyseQuery[i].equalsIgnoreCase("update")){

            }
            else if(analyseQuery[i].equalsIgnoreCase("delete")){

            }
            else{

            }*/
        // }
        return output;
    }

    public static String removeSemiColon(String inputString){
        String outputString = "";
        if (inputString!=null && !inputString.isEmpty() && inputString.charAt(inputString.length() - 1) == ';') {
            outputString = inputString.substring(0, inputString.length() - 1);
        }
        else {
            outputString = inputString;
        }
        return outputString.trim();
    }
}
