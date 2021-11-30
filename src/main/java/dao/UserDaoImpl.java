package dao;

import model.User;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.HashMap;
import java.util.Scanner;

public class UserDaoImpl implements userDao {

	@Override
	public void append(User user) {
		String user_info = user.getUser_id()+"#"+user.getUsername()+"#"+user.getPassword()+"#"+user.getSecurity_question1()+"#"+user.getSecurity_answer1()+"#"+user.getSecurity_question2()+"#"+user.getSecurity_answer2()+"#"+user.getSecurity_question3()+"#"+user.getSecurity_answer3();
		FileWriter myWriter;
		try {
			myWriter = new FileWriter("User_Profile.txt", true);
			myWriter.write(user_info);
			myWriter.write("\n");
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public HashMap<String, Object> readfile(String filename) {
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		try {
		      File myObj = new File(filename);
		      Scanner myReader = new Scanner(myObj);
			  while (myReader.hasNextLine()) {
		        User user = createUserObject(myReader.nextLine().split("[#]",0));
				dataMap.put(user.getUser_id(), (Object) user);
		      }
			 myReader.close();
		} catch (FileNotFoundException e) {
		  System.out.println("An error occurred.");
		  e.printStackTrace();
		}
		return dataMap;
	}
	private User createUserObject(String [] data) {
		User user = new User();
		user.setUser_id(data[0]);
		user.setUsername(data[1]);
		user.setPassword(data[2]);
		user.setSecurity_question1(data[3]);
		user.setSecurity_answer1(data[4]);
		user.setSecurity_question2(data[5]);
		user.setSecurity_answer2(data[6]);
		user.setSecurity_question3(data[7]);
		user.setSecurity_answer3(data[8]);
		return user;
	}
}
