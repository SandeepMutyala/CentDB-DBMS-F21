package erdGeneration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.GlobalSessionDetails;

public class generateERD {
	 public static void createERD(String dbName) {
		 BufferedReader br = null;
		 FileWriter myWriter;
		 String filePath = GlobalSessionDetails.getLoggedInUsername()+"/"+dbName+"/schemaDetails.txt";
		 String fileOne = GlobalSessionDetails.getLoggedInUsername()+"/"+dbName+"ERD.txt";
		 try {
		 myWriter = new FileWriter(fileOne, true);
		 br = new BufferedReader(new FileReader(filePath));
		 String data = null;
		 List<String> columnNamesList = new ArrayList<String>();
		 boolean fetchTableName = false;
		 String line = br.readLine();
		 while (line != null) {
			 	if (fetchTableName) {
					System.out.println("Inside fetch table name");
					columnNamesList = Arrays.asList(line.split("#"));
					for (String str: columnNamesList) {
						myWriter.write(str);
						myWriter.write('\n');	
					}
					fetchTableName = false;
				}
			 	
				if (line.matches("\\[{1}\\w*\\]")) {
					myWriter.write(line);
					fetchTableName = true;
					
				}
				
				myWriter.write('\n');
				line = br.readLine();
			}
		 
			br.close();
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
}
	 }

