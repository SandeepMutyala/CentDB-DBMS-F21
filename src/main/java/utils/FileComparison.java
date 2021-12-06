package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileComparison {
		public static Boolean compareFiles(File file1, File file2) throws IOException {
			BufferedReader permanentFileReader = new BufferedReader(new FileReader (file1));
			BufferedReader newFileReader = new BufferedReader(new FileReader (file2));
			String         line1 = null;
			String         line2 = null;
			StringBuilder  permanentStringBuilder = new StringBuilder();
			StringBuilder  temporaryStringBuilder = new StringBuilder();
			
			try {
		        while((line2 = newFileReader.readLine()) != null) {
		        	temporaryStringBuilder.append(line2);
		        	
		        }
		        while((line1 = permanentFileReader.readLine()) != null) {
		        	permanentStringBuilder.append(line1);
		        }
		        System.out.println(temporaryStringBuilder.toString());
		        System.out.println(permanentStringBuilder.toString());
			}
			finally {
				newFileReader.close();
				permanentFileReader.close();
			}
			
			if(temporaryStringBuilder.toString().contains(permanentStringBuilder.toString())) {
				return true;
			}else {
				return false;
			}
			
		}
}
