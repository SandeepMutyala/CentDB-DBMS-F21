package com.user_interface;

import com.security.UserLogin;
import com.transactions.TransactionManagement;

import dao.Analy;
import erdGeneration.generateERD;
import utils.Constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class UserInterface {
	public static void userView() throws IOException {
		Boolean isSuccessResponse = false;
		int choice = 0;
		Scanner sc = new Scanner(System.in);
		System.out.println("1: Register");
		System.out.println("2: Login");
		System.out.println("3: Exit");
		choice = sc.nextInt();
		switch (choice) {
		case 1:
			isSuccessResponse = true;// UserRegistration.registerUser();
			break;
		case 2:
			isSuccessResponse = UserLogin.userLogin();
			break;
		case 3: // exit
			break;
		default:
			System.out.println(Constants.INVALID_SELECTION);
			break;
		}
		while (isSuccessResponse) {
			System.out.println(
					"\n======================================================================================\n======================================================================================\n");
			System.out.println("Please select an option to perform an action");
			System.out.println("1: Write Queries");
			System.out.println("2: Export");
			System.out.println("3: Data Model");
			System.out.println("4: Analytics");
			System.out.println("5: Log out");
			choice = sc.nextInt();
			switch (choice) {
			case 1:
				TransactionManagement.executeQuery();
				// writeQueries();
				break;
			case 2: // handleExport();
				break;
			case 3:
				System.out.println("Please enter the database name to generate ERD");
				Scanner scanner = new Scanner(System.in);
				String dbName = scanner.nextLine().toLowerCase();
				generateERD.createERD(dbName);
				break;
			case 4: // performAnalytics();
				Analy.analysis();
				break;
			case 5:
				isSuccessResponse = false;
				break;
			default:
				System.out.println(Constants.INVALID_SELECTION);
				break;
			}
		}
	}
}
