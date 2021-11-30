package com;

import com.security.UserLogin;
import com.security.UserRegistration;
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
        switch (choice) {
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
        }
        while (isSuccessResponse) {
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
        }
    }
}