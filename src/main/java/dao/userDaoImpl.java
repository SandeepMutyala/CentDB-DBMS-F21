package dao;

import model.User;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class userDaoImpl implements userDao {

	@Override
	public void append(User user) {
		String user_info = user.getUser_id()+","+user.getUsername()+","+user.getPassword()+","+user.getSecurity_answer1()+","+user.getSecurity_answer2();
		FileWriter myWriter;
		// TODO Auto-generated method stub
			//append user data to file	
			try {
				myWriter = new FileWriter("User_Profile.txt", true);
				myWriter.write(user_info);
				myWriter.write("\n");
				myWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
			
	

	@Override
	public void readfile(String filename) {
		try {
		      File myObj = new File(filename);
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        System.out.println(data);
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		
	}
		
}
