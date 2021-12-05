package dao;

import Validations.DatabaseExists;
import Validations.DatatypeValidation;
import Validations.TableExistence;
import utils.Constants;
import utils.FileWriterClass;
import utils.GlobalSessionDetails;
import utils.SchemaDetails;
import utils.CreateStructureAndDataExportFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.FileWriterClass.createDuplicateCopy;

public class DatabaseOperationsImpl implements DatabaseOperations {
	static Formatter fmt = new Formatter();
	/*
	 * result=1, db successfully created result=2, db already exists result=3, Table
	 * created successfully result=4, Table creation failed
	 */
	@Override
	public  int createDb(String query, Boolean isTransaction) throws IOException {
		int result=0;
		String[] analyseQuery=query.split(" ");
		String dbPath = isTransaction ? GlobalSessionDetails.loggedInUsername + "/temp"
				: GlobalSessionDetails.loggedInUsername + "/";
		String directoryPath=dbPath.concat(analyseQuery[2]);
		GlobalSessionDetails.dbInAction = isTransaction ? "temp" + analyseQuery[2] : analyseQuery[2];

		File directory = new File(directoryPath);
		if (!directory.exists()){
			directory.mkdirs();
			if (isTransaction) {
				String permanantDirectoryPath = GlobalSessionDetails.loggedInUsername + "/".concat(analyseQuery[2]);
				File permanantDirectory = new File(permanantDirectoryPath);
				if (permanantDirectory.exists()) {
					System.out.println("The requested Database already exists");
					// createDuplicateCopy(directory, permanantDirectory);
				} else if (!directory.exists()) {
					directory.createNewFile();
				} else {
					System.out.println("Database created");
				}
			}
			CreateStructureAndDataExportFile.structureAndDataExportFileCreation(directoryPath);
			if(CreateStructureAndDataExportFile.insertInStructureAndDataExportFile(query,directoryPath)){
				result=1;
			}
		}
		return result;
	}

	@Override
	public int createTable(String query, Boolean isTransaction) {
		int result = 0;
		// String tableDiectoryPath = GlobalSessionDetails.loggedInUsername + "/";
		String tablePath = "";
		String tempTablePath = "";
		String dbName = "";
		String tableName = "";
		String[] columnName;
		String[] columnDataType;

		// Pattern to get table name and DBname
		Pattern tablePattern = Pattern.compile(Constants.DB_TABLE_NAME_CREATE_SEPARATOR_PATTERN,
				Pattern.CASE_INSENSITIVE);
		Matcher matchResult = tablePattern.matcher(query);

		try {
			if (matchResult.find()) {
				String[] separateDbtableName = matchResult.group(1).split("\\.");

				// Primary key is must
				String[] columnDetails = matchResult.group(2).substring(1, matchResult.group(2).length() - 1)
						.split(",");
				String[] removedPrimaryKeyColumnDetails = new String[columnDetails.length - 1];
				for (int pi = 0; pi < columnDetails.length - 1; pi++) {
					removedPrimaryKeyColumnDetails[pi] = columnDetails[pi];
				}
				columnDataType = fetchColumnDataType(removedPrimaryKeyColumnDetails);
				System.out.println(columnDataType[0]);
				columnName = fetchColumnName(removedPrimaryKeyColumnDetails);

				if (DatatypeValidation.validateTableDataType(columnDataType)) {
					// if database name is not in the query
					if (separateDbtableName.length <= 1) {
						dbName = GlobalSessionDetails.getDbInAction();
						tableName = matchResult.group(1);
						tempTablePath = GlobalSessionDetails.getLoggedInUsername()
								.concat("/" + dbName + "/" + tableName) + ".txt";
						tablePath = GlobalSessionDetails.getLoggedInUsername().concat("/" + dbName + "/" + tableName)
								+ ".txt";

					}
					// if database name is in query
					if (separateDbtableName.length > 1) {
						dbName = isTransaction? "temp"+separateDbtableName[0] : separateDbtableName[0];
						tableName = separateDbtableName[1];
						tempTablePath = GlobalSessionDetails.getLoggedInUsername()
								.concat("/" + dbName + "/" + tableName) + ".txt";
						tablePath = GlobalSessionDetails.getLoggedInUsername().concat("/" + dbName + "/" + tableName)
								+ ".txt";
					}

					if (isTransaction) {
						if (DatabaseExists.validateDatabaseExistence(dbName)) {
							// create Temporary Table
							String permanentTablePath = GlobalSessionDetails.getLoggedInUsername()
									.concat("/" + dbName.substring(4) + "/" + tableName) + ".txt";
							File permanentTable = new File(permanentTablePath);
							if (permanentTable.exists()) {
								System.out.println("create");
								File tempTable = new File(tempTablePath);
								FileWriterClass.createDuplicateCopy(tempTable, permanentTable);
							}
							result = createTableFile(dbName, tempTablePath, tableName, columnDataType, columnName,
									query, isTransaction);
						} else {
							System.out.println("Please create or choose a Database first");
						}
					} else {
						if (DatabaseExists.validateDatabaseExistence(dbName)) {
							// create Table
							result = createTableFile(dbName, tablePath, tableName, columnDataType, columnName, query,
									isTransaction);
						} else {
							System.out.println("Please create or choose a Database first");
						}
					}

				} else {
					System.out.println("Please select valid datatype");
				}
			} else {
				System.out.println("Please check the syntax for table creation");
			}
		} catch (Exception ex) {
			System.out.println("Please check the syntax for table creation" + ex);
			result = 4;
		} finally {
			return result;
		}
	}

	public static String[] fetchColumnDataType(String[] columnDetails) {
		String[] columnsDataType = new String[columnDetails.length];
		for (int i = 0; i < columnDetails.length; i++) {

			String[] columnData = columnDetails[i].trim().split(" ");
			columnsDataType[i] = columnData[1];
		}
		return columnsDataType;
	}

	public static String[] fetchColumnName(String[] columnDetails) {
		String[] columnName = new String[columnDetails.length];
		for (int i = 0; i < columnDetails.length; i++) {
			String[] columnData = columnDetails[i].trim().split(" ");
			columnName[i] = columnData[0];
		}
		return columnName;
	}

	public int createTableFile(String dbName,String tablePath,String tableName,String[] columnDataType,String[] columnName, String query, Boolean isTransaction) throws IOException {
		String tableDiectoryPath=GlobalSessionDetails.getLoggedInUsername()+"/" + dbName+"/";
		System.out.println("tableDiectoryPath");
		File tableFile = new File(tablePath);
		int result=0;
		System.out.println("in create table line 172"+ tableFile.isFile());
		if(!tableFile.isFile()){
			tableFile.createNewFile();
			if(CreateStructureAndDataExportFile.insertInStructureAndDataExportFile(query,tableDiectoryPath)){
				System.out.println("in create table line 175");
				// write logic to extract column and datatype from query
				if(SchemaDetails.createSchemaFile(tableDiectoryPath)){
					String formattedColumnDetailsInFile=mergeColumnNameAndValue(columnName,columnDataType,"CREATE");
					FileWriterClass.writeInFile("["+tableName.trim().toLowerCase()+"]",tableDiectoryPath+"/schemaDetails.txt");
					SchemaDetails.insertInSchemaFile(formattedColumnDetailsInFile,tableDiectoryPath, isTransaction);

				}
				result=3;
			}
		}else if (isTransaction) {
				if (CreateStructureAndDataExportFile.insertInStructureAndDataExportFile(query, tableDiectoryPath)) {
					System.out.println("in create table line 175");
					// write logic to extract column and datatype from query
					if (SchemaDetails.createSchemaFile(tableDiectoryPath)) {
						String formattedColumnDetailsInFile = mergeColumnNameAndValue(columnName, columnDataType, "CREATE");
						FileWriterClass.writeInFile("[" + tableName.trim().toLowerCase() + "]", tableDiectoryPath + "/schemaDetails.txt");
						SchemaDetails.insertInSchemaFile(formattedColumnDetailsInFile, tableDiectoryPath, isTransaction);

					}
				}
				result = 3;
			} else {
			System.out.println("Table Already exists");
			}
		return result;
	}

	public String mergeColumnNameAndValue(String[] columnNames,String[] columnValues,String operationType){
		StringBuilder insertStringInFile=new StringBuilder();
		for(int i=0;i<columnNames.length;i++){
			if(i==columnNames.length-1){
				insertStringInFile.append(columnNames[i]+":"+columnValues[i]);
			}
			else{
				insertStringInFile.append(columnNames[i]+":"+columnValues[i]+"#");
			}

		}
		String formattedInsertStringInFile = insertStringInFile.toString().replaceAll(" ", "");
		formattedInsertStringInFile = insertStringInFile.toString().replaceAll(";", "");
		return formattedInsertStringInFile;
	}

	@Override
	public int insertInTable(String query, Boolean isTransaction) throws IOException{
		int result=0;
		String dbName="";
		String tableName="";
		String schemaDetailPath="";

		// to spearate table and database name pattern
		Pattern tablePattern = Pattern.compile(Constants.DB_TABLE_NAME_INSERT_SEPARATOR_PATTERN, Pattern.CASE_INSENSITIVE);
		Matcher matchTableResult = tablePattern.matcher(query);

		// to fetch column name from the query pattern
		Pattern ColumnValuePattern = Pattern.compile(Constants.COLUMN_NAME_VALUES_SEPARATOR_PATTERN, Pattern.CASE_INSENSITIVE);
		Matcher matchColumnValueResult = ColumnValuePattern.matcher(query);
		//System.out.println("Q"+matchColumnValueResult.group(1));
		if(matchTableResult.find()){
			System.out.println("221");
			String[] separateDbtableName=matchTableResult.group(1).split("\\.");

			if(separateDbtableName.length==2){
				dbName=isTransaction? "temp"+separateDbtableName[0] : separateDbtableName[0];
				tableName=separateDbtableName[1];

				if(DatabaseExists.validateDatabaseExistence(dbName) && TableExistence.checkIfTableExists(dbName,tableName)){
					String insertFilePath = GlobalSessionDetails.loggedInUsername + "/" + dbName + "/" + tableName + ".txt";
					if(matchColumnValueResult.find()){
						String[] columnNames=matchColumnValueResult.group(1).split(",");
						String[] columnValues=matchColumnValueResult.group(2).split(",");
						// checking if values and table column length entered by user is correct or not
						if(columnNames.length==columnValues.length){
							String formattedInsertStringInFile=mergeColumnNameAndValue(columnNames,columnValues,"INSERT");
							if(isTransaction) {
								String permanentTablePath = GlobalSessionDetails.getLoggedInUsername()
										.concat("/" + dbName.substring(4) + "/" + tableName) + ".txt";
								File permanentTable = new File(permanentTablePath);
								if (permanentTable.exists()) {
									File tempTable = new File(insertFilePath);
									FileWriterClass.createDuplicateCopy(tempTable, permanentTable);
									FileWriterClass.writeInFile(formattedInsertStringInFile, insertFilePath);
								}
								else {
									FileWriterClass.writeInFile(formattedInsertStringInFile, insertFilePath);
								}
							}else {
								FileWriterClass.writeInFile(formattedInsertStringInFile, insertFilePath);
							}
							// write validation logic before inserting into text file and match if table count is not equal to total length of total columns availabe
							// in table then enter null for each column.
							result=5;
							//System.out.println(insertStringInFile+"Hello string");
						}
						else{
							result=6;
							System.out.println("Expecting "+columnNames.length+" values instead got "+columnValues.length);
						}
					}

				}
			}
			else{
				result=6;
				System.out.println("Please check insert syntax. Dbname not included.");
			}

		}

		return result;
	}

	@Override
	public int fetchTableRecords(String query, Boolean isTransaction) throws Exception {
		int result=0;
		String dbName="";
		String tableName="";
		String[] columnInQuery;
		boolean validateProvidedLegitColumns=true;

		// Logic to extract table name and database name
		Pattern patternDBTable=Pattern.compile(Constants.DB_TABLE_NAME_SELECT_SEPARATOR_PATTERN, Pattern.CASE_INSENSITIVE);
		Matcher matcherDBTable = patternDBTable.matcher(query);

		// Logic to extract columns
		Pattern columnsPattern = Pattern.compile("select(.*?)from", Pattern.CASE_INSENSITIVE);
		Matcher columnsMatcher = columnsPattern.matcher(query);

		if(matcherDBTable.find() && columnsMatcher.find()){
			String[] separateDbtableName=matcherDBTable.group(0).split("\\.");

			// if query contains dbName and table name
			if(separateDbtableName.length==2){
				dbName=separateDbtableName[0].trim();
				tableName=separateDbtableName[1].trim();
				columnInQuery=columnsMatcher.group(1).split(",");
				List<String> totalColumn=Arrays.asList(readColumnsOfTable(dbName,tableName)); // total columns present in Table

				// to check if column provided by user matches with table columns in DB
				if(columnInQuery.length>1){
					for(int i=0;i<columnInQuery.length;i++){
						if(!totalColumn.contains(columnInQuery[i].trim())){
							validateProvidedLegitColumns=false;
							break;
						}
					}
				}

				// looping to print the fetched records
				if(validateProvidedLegitColumns){
					// write a logic to read from the file
					List<List<String>> tableRecords=readFile(dbName,tableName,columnInQuery);
					fmt.format("\n");
					for (int i = 0; i < tableRecords.size(); i++) {
						for(int j=0;j<tableRecords.get(i).size();j++){
							fmt.format("%30s", tableRecords.get(i).get(j));
						}
						fmt.format("\n");
					}
					System.out.println(fmt);
					result=7;
				}else{
					result=8;
					System.out.println("Provided Columns does not exists in table");
				}
			}
			else{
				result=8;
				System.out.println("Either provide dbName or use useDB operation");
			}
		}
		else{
			result=8;
			System.out.println("Please provide valid select syntax");
		}
		return result;
	}


	public List<List<String>> readFile(String dbName, String tableName, String[]columnsInQuery) throws Exception {
		String path = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/" + tableName + ".txt";
		List<List<String>> tableRecords = new ArrayList<List<String>>();
		// String[]columnsInQuery= Arrays.asList();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = reader.readLine();
		int counter = 0;

		// reading table file line by lilne for select output
		while (line != null) {
			String[] splitSt = line.split("#");
			List<String> rowValue=new ArrayList<String>();
			for (int i = 0; i < splitSt.length; i++) {
				String[] columnNameValueSeparator = splitSt[i].trim().split(":");
				if(columnNameValueSeparator.length>0){

					// if columnNames are provided or  user want to fetch all (*) records
					if(!columnsInQuery[0].trim().equals("*")){
						for(int ci=0;ci<columnsInQuery.length;ci++){
							if(columnsInQuery[ci].trim().equals(columnNameValueSeparator[0])){
								if (counter == 0) {
									fmt.format("%30s", columnNameValueSeparator[0]);
								}
								rowValue.add(columnNameValueSeparator[1]);
							}
						}

					}else{
						// if requested for all records
						if (counter == 0) {
							// adding here output heading which will be column name
							fmt.format("%30s", columnNameValueSeparator[0]);
						}
						rowValue.add(columnNameValueSeparator[1]);
					}
				}


			}
			tableRecords.add(rowValue);

			// setting here counter as 1 so that fmt does not contain header in a loop.
			counter = 1;
			line = reader.readLine();
		}
		reader.close();
		return tableRecords;
	}

	public String[] readColumnsOfTable(String dbName,String tableName) throws Exception {
		String tablePath = GlobalSessionDetails.getLoggedInUsername()+"/"+dbName+"/schemaDetails.txt";
		List<String> columnNamesList = new ArrayList<String>();
		boolean fetchTableName=false;
		BufferedReader reader = new BufferedReader(new FileReader(tablePath));
		String line = reader.readLine();
		int counter = 0;
		while (line != null) {
			if(fetchTableName){
				columnNamesList= Arrays.asList(line.split("#"));
				fetchTableName=false;
				break;
			}
			if(line.contains("["+tableName+"]")){
				fetchTableName=true;
			}

			line = reader.readLine();
		}
		reader.close();
		String[] columnName = new String[columnNamesList.size()];
		if(columnNamesList.size()>0){
			for(int i=0;i<columnNamesList.size();i++){
				columnName[i]=columnNamesList.get(i).split(":")[0];
			}
		}

		return columnName;
	}

	@Override
	public void updateATableRecords() {

	}

	@Override
	public void deleteATableRecords() {

	}

	@Override
	public void deleteTable() {

	}

	@Override
	public void useDb() {
		// TODO Auto-generated method stub

	}

}

/*
 * Not Used Code /*Pattern tablePattern =
 * Pattern.compile(".*create\\s+table\\s+`([a-zA-Z0-9_]*?)`.`([a-zA-Z0-9_]*?)`",
 * Pattern.CASE_INSENSITIVE); Matcher matchResult = tablePattern.matcher(query);
 */

//System.out.println(matchResult.find()+" file path "+matchResult.group(2)+" "+matchResult.group(1)+matchResult.group(0));
//if user enters schemaName alaongwith tabelName in the query
/*
 * if(separateDbtableName.length==0){
 * tablePath=tableDiectoryPath.concat(analyseQuery[2])+".txt"; }
 * if(separateDbtableName.length>1){
 * tablePath=tableDiectoryPath.concat(separateDbtableName[1])+".txt"; }
 * 
 * // if user enters schemaName alaongwith tabelName in the query
 * /*if(separateDbtableName.length==0){
 * tablePath=tableDiectoryPath.concat(analyseQuery[2])+".txt"; }
 * if(separateDbtableName.length>1){
 * tablePath=tableDiectoryPath.concat(separateDbtableName[1])+".txt"; }
 */
