package EDRGeneration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import utils.GlobalSessionDetails;

public class generateERD {
	 public static void createERD(String dbName) throws FileNotFoundException {
		 BufferedReader br = null;
		 FileWriter myWriter;
		 String filePath = GlobalSessionDetails.getLoggedInUsername()+"/"+dbName+"/schemaDetails.txt";
		 br = new BufferedReader(new FileReader(filePath));
		 String data = null;
		 try {
			while ((data = br.readLine()) != null) {
				if (data.matches("\\[{1}\\w*\\]")) {
					 String fileOne = GlobalSessionDetails.getLoggedInUsername()+"/"+dbName+"ERD";
					 myWriter = new FileWriter(fileOne, true);
					 myWriter.write(data);
				}
			 }
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
}
