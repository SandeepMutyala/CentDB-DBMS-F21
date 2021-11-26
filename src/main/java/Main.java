import java.util.Scanner;

import dao.userDaoImpl;
import model.User;

public class Main {
    public static void main(String[] args) {
    	userDaoImpl udi = new userDaoImpl();
        init.ReadFileSystem.initialize();
        User user = new User();
        user.setUser_id("xyz000");
        user.setUsername("thakkar0648");
        user.setPassword("1q2w3e4r5t6y");
        user.setSecurity_answer1("mom");
        user.setSecurity_answer2("dad");
        udi.append(user);
        //int choice = 0;
		/*
		 * Scanner sc = new Scanner(System.in); //if login map is empty System.out.
		 * println("You need to register in order to proceed. Please choose a number from the options below"
		 * ); System.out.println("1: Register"); System.out.println("2: Login");
		 * System.out.println("3: Exit"); choice = sc.nextInt(); switch (choice) { case
		 * 1: security.UserRegistration.registerUser(); break; case 2: //login break;
		 * case 3: //exit break; }
		 * 
		 * while (true) { System.out.
		 * println("Please select an option by entering the number corresponding to the activity you want to perform."
		 * ); System.out.println("1: Write Queries"); System.out.println("2: Export");
		 * System.out.println("3: Data Model"); System.out.println("4: Analytics");
		 * choice = sc.nextInt(); switch (choice) { case 1: //writeQueries(); break;
		 * case 2: //handleExport(); break; case 3: //handleDataModel(); break; case 4:
		 * //performAnalytics(); break; } }
		 */
    }
}