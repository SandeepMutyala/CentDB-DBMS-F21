import com.security.UserLogin;
import com.security.UserRegistration;
import dao.DatabaseOperations;
import dao.DatabaseOperationsImpl;
import utils.Constants;
import utils.GlobalSessionDetails;
import utils.QueryAnalyzer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Boolean isSuccessResponse = false;
        int choice = 0;
        Scanner sc = new Scanner(System.in);
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("3: Exit");
        choice = sc.nextInt();
        /*switch (choice) {
            case 1:
                isSuccessResponse = UserRegistration.registerUser();
                break;
            case 2:
                isSuccessResponse = UserLogin.userLogin();
                break;
            case 3: //exit
                break;
            default:
                System.out.println(Constants.INVALID_SELECTION);
                break;
        }*/
        try{
            //while (isSuccessResponse) {
            System.out.println("\n======================================================================================\n======================================================================================\n");
            System.out.println("Please select an option to perform an action");
            System.out.println("1: Write Queries");
            System.out.println("2: Export");
            System.out.println("3: Data Model");
            System.out.println("4: Analytics");
            System.out.println("5: Log out");
            choice = sc.nextInt();
            switch (choice) {
                case 1: //writeQueries();
                    Scanner scQuery = new Scanner(System.in);
                    String query = scQuery.nextLine();
                    DatabaseOperations dbOperations=new DatabaseOperationsImpl();
                    queryOutputAnalysis(QueryAnalyzer.splitQuery(query,dbOperations));
                    //SplitQuery.splitQuery(query, GlobalSessionDetails.loggedInUsername);
                    break;
                case 2: //handleExport();
                    break;
                case 3: //handleDataModel();
                    break;
                case 4: //performAnalytics();
                    break;
                case 5: isSuccessResponse = false;
                    break;
                default: System.out.println(Constants.INVALID_SELECTION);
                    break;
            }
            // }
        }catch(Exception ex){
            System.out.println("Exceptiton occured"+ex);
        }

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
            default: System.out.println("Couldn't perform Operation");
        }
    }
}