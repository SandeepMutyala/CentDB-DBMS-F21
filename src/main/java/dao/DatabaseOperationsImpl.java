package dao;

import Validations.DatabaseExists;
import Validations.DatatypeValidation;
import Validations.TableExistence;

import utils.*;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.FileComparison.compareFiles;
import static utils.FileWriterClass.createDuplicateCopy;


public class DatabaseOperationsImpl implements DatabaseOperations {

	Formatter fmt = new Formatter();

	@Override
	public int createDb(String query, Boolean isTransaction) throws IOException {
		int result = 0;
		String[] analyseQuery = query.split(" ");
        String dbName = isTransaction ?"temp"+analyseQuery[2]:analyseQuery[2];
        String directoryPath=GlobalSessionDetails.getLoggedInUsername()+"/"+dbName;
		GlobalSessionDetails.dbInAction = isTransaction ? "temp" + analyseQuery[2] : analyseQuery[2];

		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdirs();
			if (isTransaction) {
				String permanantDirectoryPath = GlobalSessionDetails.loggedInUsername + "/".concat(analyseQuery[2]);
				File permanantDirectory = new File(permanantDirectoryPath);
				if (permanantDirectory.exists()) {
					System.out.println("The requested Database already exists");
					String permanentStructureAndDataExport = GlobalSessionDetails.getLoggedInUsername()
                            .concat("/" + dbName.substring(4) + "/structureAndDataExport") + ".txt";
                    String permanentSchemaDetails = GlobalSessionDetails.getLoggedInUsername()
                            .concat("/" + dbName.substring(4) + "/schemaDetails") + ".txt";
                    File permanentStructureAndDataExportFile = new File(permanentStructureAndDataExport);
                    File permanentSchemaDetailsFile = new File(permanentSchemaDetails);
                    String schemaDetailsTempPath = GlobalSessionDetails.getLoggedInUsername()
                            .concat("/" + dbName + "/schemaDetails") + ".txt";
                    String structureAndDataExport = GlobalSessionDetails.getLoggedInUsername()
                            .concat("/" + dbName + "/structureAndDataExport") + ".txt";
                    File schemaDetailsTempPathFile = new File(schemaDetailsTempPath);
                    File structureAndDataExportFile = new File(structureAndDataExport);
					// createDuplicateCopy(directory, permanantDirectory);
                    FileWriterClass.createDuplicateCopy(schemaDetailsTempPathFile, permanentSchemaDetailsFile);
                    FileWriterClass.createDuplicateCopy(structureAndDataExportFile, permanentStructureAndDataExportFile);
				} else {
					System.out.println("Database created");
				}
			}else {
            CreateStructureAndDataExportFile.structureAndDataExportFileCreation(dbName);
            SchemaDetails.createSchemaFile(dbName);
            if(CreateStructureAndDataExportFile.insertInStructureAndDataExportFile(query,dbName)){
                result=1;
            }}
		}
		return result;
	}

	@Override
	public int createTable(String query, Boolean isTransaction) {
		int result = 0;
		// String tableDiectoryPath = GlobalSessionDetails.loggedInUsername + "/";
		String tablePath = "";
		//String tempTablePath = "";
		String dbName = "";
		String tableName = "";
		String[] columnName;
		String[] columnDataType;
        String primaryKey="";

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
                primaryKey=columnDetails[columnDetails.length-1];

				for (int pi = 0; pi < columnDetails.length - 1; pi++) {
					removedPrimaryKeyColumnDetails[pi] = columnDetails[pi];
				}
				columnDataType = fetchColumnDataType(removedPrimaryKeyColumnDetails);
				//System.out.println(columnDataType[0]);
				columnName = fetchColumnName(removedPrimaryKeyColumnDetails);

				if (DatatypeValidation.validateTableDataType(columnDataType)) {
                    if(validatePrimaryKey(primaryKey,columnName)){
                        if (separateDbtableName.length == 2) {
                            dbName = isTransaction?"temp"+separateDbtableName[0].trim():separateDbtableName[0].trim();
                            tableName = separateDbtableName[1].trim().toLowerCase();
                            LogPrinter print = LogPrinter.getInstanceObject();
                            print.queryMessagePrinter("The table named "+ tableName + " has been created by " + GlobalSessionDetails.loggedInUsername+ " in the database named "+ dbName+ ".");
                            print.generalMessagePrinter("The database "+ dbName + " created by " + GlobalSessionDetails.loggedInUsername+ " has ","table", dbName);

                        } else if (!GlobalSessionDetails.getDbInAction().isEmpty()) {
                            dbName = GlobalSessionDetails.getDbInAction().trim();
                            tableName = matchResult.group(1).trim().toLowerCase();
                            //System.out.println(tableName);
                        } else {
                            result = 4;
                            System.out.println("Either provide dbName or use useDB operation");
                            LogPrinter print = LogPrinter.getInstanceObject();
                            print.errorPrinter("Database "+ dbName + " provided by " + GlobalSessionDetails.getLoggedInUsername() +  " doesn't exist.");
                        }

                        if(!dbName.isEmpty()){
                            if (isTransaction) {
                                if (DatabaseExists.validateDatabaseExistence(dbName)) {
                                    // create Temporary Table
                                    System.out.println("On line 107");
                                    String permanentTablePath = GlobalSessionDetails.getLoggedInUsername()
                                            .concat("/" + dbName.substring(4) + "/" + tableName) + ".txt";  
                                    File permanentTable = new File(permanentTablePath);
                                    if (permanentTable.exists()) {
                                        System.out.println("create");
                                        tablePath =GlobalSessionDetails.getLoggedInUsername()
                                                .concat("/" + dbName + "/" + tableName) + ".txt";
                                        File tempTable = new File(tablePath);
                                        tempTable.createNewFile();
                                        //FileWriterClass.createDuplicateCopy(tempTable, permanentTable);
                                       
                                    }else{
                                    	
                                    tablePath=GlobalSessionDetails.getLoggedInUsername().concat("/"+dbName+"/"+tableName)+".txt";
                                    result = createTableFile(dbName, tablePath, tableName, columnDataType, columnName,
                                            query, isTransaction,primaryKey);
                                }
                            } 
                                
                            }else {
                                if (DatabaseExists.validateDatabaseExistence(dbName)) {
                                    tablePath=GlobalSessionDetails.getLoggedInUsername().concat("/"+dbName+"/"+tableName)+".txt";
                                    result=createTableFile(dbName,tablePath,tableName,columnDataType,columnName,query,isTransaction,primaryKey.trim());
                                }
                            }
                        }
                    }else{
                        result=4;
                        System.out.println("Primary key provided does not exists in column definitaion");
                        System.out.println("Primary key provided does not exists in column definition");
                        LogPrinter print = LogPrinter.getInstanceObject();
                        print.errorPrinter("Primary key doesn't exist in the database "+dbName+" provided by user " + GlobalSessionDetails.loggedInUsername);
                    }


				} else {
					System.out.println("Please select valid datatype");
				}
			} else {
				System.out.println("Please check the syntax for table creation");
                LogPrinter print = LogPrinter.getInstanceObject();
                print.errorPrinter("Syntax given by "+ GlobalSessionDetails.getLoggedInUsername() + " for table creation is not valid.");
			}
		 
			}catch (Exception ex) {
			System.out.println("Please check the syntax for table creation" + ex);
			result = 4;
		} finally {
			return result;
		}
	}

    public static String[] fetchColumnDataType(String[] columnDetails){
        String[] columnsDataType=new String[columnDetails.length];
        for(int i=0;i<columnDetails.length;i++){

            String[] columnData=columnDetails[i].trim().split(" ");
            columnsDataType[i]=columnData[1];
        }
        return columnsDataType;
    }

    public static String[] fetchColumnName(String[] columnDetails){
        String[] columnName=new String[columnDetails.length];
        for(int i=0;i<columnDetails.length;i++){
            String[] columnData=columnDetails[i].trim().split(" ");
            columnName[i]=columnData[0];
        }
        return columnName;
    }

	public int createTableFile(String dbName, String tablePath, String tableName, String[] columnDataType,
			String[] columnName, String query, Boolean isTransaction,String primaryKey) throws IOException {
		String tableDiectoryPath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/";

		File tableFile = new File(tablePath);
		int result = 0;
		//System.out.println("in create table line 172" + tableFile.isFile());
		if (!tableFile.isFile()) {
			tableFile.createNewFile();
			if (CreateStructureAndDataExportFile.insertInStructureAndDataExportFile(query, dbName)) {
				// write logic to extract column and datatype from query
				//if (SchemaDetails.createSchemaFile(tableDiectoryPath)) {
					String formattedColumnDetailsInFile = mergeColumnNameAndValue(columnName, columnDataType);
					FileWriterClass.writeInFile("[" + tableName.trim().toLowerCase() + "]",
							tableDiectoryPath + "/schemaDetails.txt");
					SchemaDetails.insertInSchemaFile(formattedColumnDetailsInFile,dbName, isTransaction);
                    FileWriterClass.writeInFile("["+primaryKey+"]",tableDiectoryPath+"/schemaDetails.txt");

				//}
				result = 3;
			}
		} else if (isTransaction) {
			if (CreateStructureAndDataExportFile.insertInStructureAndDataExportFile(query, dbName)) {
				// write logic to extract column and datatype from query
				//if (SchemaDetails.createSchemaFile(dbName)) {
                    String formattedColumnDetailsInFile = mergeColumnNameAndValue(columnName, columnDataType);
                    FileWriterClass.writeInFile("[" + tableName.trim().toLowerCase() + "]",
                            tableDiectoryPath + "/schemaDetails.txt");
                    SchemaDetails.insertInSchemaFile(formattedColumnDetailsInFile,dbName, isTransaction);
                    FileWriterClass.writeInFile("["+primaryKey+"]",tableDiectoryPath+"/schemaDetails.txt");

			//	}
			}
			result = 3;
		} else {
			System.out.println("Table Already exists");
            LogPrinter print = LogPrinter.getInstanceObject();
            print.errorPrinter("Table created by "+ GlobalSessionDetails.getLoggedInUsername() + " already exists the " + dbName);
		}
		return result;
	}

    public String mergeColumnNameAndValue(String[] columnNames,String[] columnValues){
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
        formattedInsertStringInFile = formattedInsertStringInFile.replaceAll(";", "");
        return formattedInsertStringInFile;
    }

	@Override
	public int insertInTable(String query, Boolean isTransaction) throws Exception {
        System.out.println(query);
        int result = 0;
        String dbName = "";
        String tableName = "";
        //String schemaDetailPath = "";
        boolean validateProvidedLegitColumns = false;

        // to spearate table and database name pattern
        Pattern tablePattern = Pattern.compile(Constants.DB_TABLE_NAME_INSERT_SEPARATOR_PATTERN,
                Pattern.CASE_INSENSITIVE);
        Matcher matchTableResult = tablePattern.matcher(query);

        // to fetch column name from the query pattern
        Pattern ColumnValuePattern = Pattern.compile(Constants.COLUMN_NAME_VALUES_SEPARATOR_PATTERN,
                Pattern.CASE_INSENSITIVE);
        Matcher matchColumnValueResult = ColumnValuePattern.matcher(query);
        // System.out.println("Q"+matchColumnValueResult.group(1));
        if (matchTableResult.find()) {
            //System.out.println("221");
            String[] separateDbtableName = matchTableResult.group(1).split("\\.");

            if (separateDbtableName.length == 2) {
                dbName = isTransaction ? "temp" + separateDbtableName[0].trim() : separateDbtableName[0].trim();
                tableName = separateDbtableName[1].trim().toLowerCase();

            } else if (!GlobalSessionDetails.getDbInAction().isEmpty()) {

                dbName = GlobalSessionDetails.getDbInAction().trim();
                tableName = matchTableResult.group(1).trim().toLowerCase();
                System.out.println(tableName);
            } else {
                result = 6;
                System.out.println("Either provide dbName or use useDB operation");
            }


            //if (separateDbtableName.length == 2) {
            //	dbName = isTransaction ? "temp" + separateDbtableName[0] : separateDbtableName[0];
            // = separateDbtableName[1];

            if (!tableName.isEmpty() && !dbName.isEmpty()) {
                if (DatabaseExists.validateDatabaseExistence(dbName)
                        && TableExistence.checkIfTableExists(dbName, tableName)) {
                    String insertFilePath = GlobalSessionDetails.loggedInUsername + "/" + dbName + "/" + tableName
                            + ".txt";
                    if (matchColumnValueResult.find()) {

                        String[] columnNames = matchColumnValueResult.group(1).split(",");
                        String[] columnValues = matchColumnValueResult.group(2).split(",");
                        List<String> totalColumn = Arrays.asList(readColumnsOfTable(dbName, tableName));

                        validateProvidedLegitColumns = validateIfColumnIsInTable(columnNames, totalColumn);

                        String primaryKey = readPrimaryKey(dbName, tableName);
                        int primaryIndex = getPrimaryKeyIndex(primaryKey, columnNames);

                        if (validateProvidedLegitColumns) {

                            if (primaryIndex != -1 && !columnValues[primaryIndex].isEmpty()) {
                                boolean checkDuplicatePrimaryKey = duplicatePrimaryKey(dbName, tableName, columnNames[primaryIndex] + ":" + columnValues[primaryIndex]);
                                if (!checkDuplicatePrimaryKey) {
                                    String formattedInsertStringInFile = mergeColumnNameAndValue(columnNames, columnValues);
                                    // checking if values and table column length entered by user is correct or not
                                    if (columnNames.length == columnValues.length) {
                                        if (isTransaction) {
                                            String permanentTablePath = GlobalSessionDetails.getLoggedInUsername()
                                                    .concat("/" + dbName.substring(4) + "/" + tableName) + ".txt";
                                            File permanentTable = new File(permanentTablePath);
                                            if (permanentTable.exists()) {
                                                File tempTable = new File(insertFilePath);
                                                if (!compareFiles(permanentTable, tempTable)) {
                                                    createDuplicateCopy(tempTable, permanentTable);
                                                }
                                                FileWriterClass.writeInFile(formattedInsertStringInFile, insertFilePath);
                                                CreateStructureAndDataExportFile.insertInStructureAndDataExportFile(query, dbName);
                                                LogPrinter print = LogPrinter.getInstanceObject();
                                                print.queryMessagePrinter("User "+ GlobalSessionDetails.getLoggedInUsername() + " inserted columns named " +matchColumnValueResult.group(1) + " in table "+ tableName+ " inside a database named "+ dbName + ".");
                                                print.generalMessagePrinter("The database "+ dbName + " created by " + GlobalSessionDetails.loggedInUsername+ " has " ,"insert",dbName);
                                            } else {
                                                FileWriterClass.writeInFile(formattedInsertStringInFile, insertFilePath);
                                                CreateStructureAndDataExportFile.insertInStructureAndDataExportFile(query, dbName);
                                            }
                                        } else {

                                            FileWriterClass.writeInFile(formattedInsertStringInFile, insertFilePath);
                                            CreateStructureAndDataExportFile.insertInStructureAndDataExportFile(query, dbName);

                                            //String directoryPath=String directoryPath=dbPath.concat(analyseQuery[2]);

                                            // write validation logic before inserting into text file and match if table count is not equal to total length of total columns availabe
                                            // in table then enter null for each column.
                                            result = 5;
                                            //System.out.println(insertStringInFile+"Hello string");
                                            LogPrinter print = LogPrinter.getInstanceObject();
                                            print.queryMessagePrinter("User "+ GlobalSessionDetails.getLoggedInUsername() + " inserted columns named " +matchColumnValueResult.group(1) + " in table "+ tableName+ " inside a database named "+ dbName + ".");
                                            print.generalMessagePrinter("The database "+ dbName + " created by " + GlobalSessionDetails.loggedInUsername+ " has " ,"insert",dbName);
                                        }

                                    } else {
                                        result = 6;
                                        System.out.println("Expecting " + columnNames.length + " values instead got " + columnValues.length);
                                        LogPrinter print = LogPrinter.getInstanceObject();
                                        print.errorPrinter("Expecting "+columnNames.length+" values instead got "+columnValues.length + " while inserting into the table.");
                                    }
                                } else {
                                    System.out.println("Primary Key Value already exists!");
                                    LogPrinter print = LogPrinter.getInstanceObject();
                                    print.errorPrinter("Primary key value already exists for the "+tableName+" which is in the database "+ dbName);
                                }

                            } else {
                                result = 6;
                                System.out.println("Primary key value not provided");
                                LogPrinter print = LogPrinter.getInstanceObject();
                                print.errorPrinter("Primary key value is not provided while creating "+tableName+" in the database "+ dbName);
                            }

                        } else {
                            result = 6;
                            System.out.println("Trying to insert column that doesn't exists in table");
                            LogPrinter print = LogPrinter.getInstanceObject();
                            print.errorPrinter("Trying to insert into column that does not in the database "+ dbName);
                        }

                    }
                }
            }
        }


return result;
    }




        @Override
        public int fetchTableRecords(String query, Boolean isTransaction) throws Exception {
            int result = 0;
            String dbName = "";
            String tableName = "";
            String[] columnInQuery;
            String whereColumnName = "";
            String whereColumnValue = "";
            boolean validateProvidedLegitColumns = true;

            // Logic to extract table name and database name
            Pattern patternDBTable = Pattern.compile(Constants.DB_TABLE_NAME_SELECT_SEPARATOR_PATTERN,
                    Pattern.CASE_INSENSITIVE);
            Matcher matcherDBTable = patternDBTable.matcher(query);

            // Logic to extract columns
            Pattern columnsPattern = Pattern.compile("select(.*?)from", Pattern.CASE_INSENSITIVE);
            Matcher columnsMatcher = columnsPattern.matcher(query);

            //to extract where condition
            Pattern wherePattern = Pattern.compile("(?<=where\\s).*", Pattern.CASE_INSENSITIVE);
            Matcher whereMatcher = wherePattern.matcher(query);

            if (matcherDBTable.find() && columnsMatcher.find()) {
                String[] separateDbtableName = matcherDBTable.group(0).split("\\.");

                // if query contains dbName and table name
                if (separateDbtableName.length == 2) {
                    dbName = isTransaction ? "temp" + separateDbtableName[0].trim() : separateDbtableName[0].trim();
                    tableName = separateDbtableName[1].trim().toLowerCase();
                } else if (!GlobalSessionDetails.getDbInAction().isEmpty()) {  // to ceck if user used useDB opeartion
                    dbName = GlobalSessionDetails.getDbInAction().trim();
                    tableName = matcherDBTable.group(0).trim().toLowerCase();
                } else {
                    result = 8;
                    System.out.println("Either provide dbName or use useDB operation");
                    LogPrinter print = LogPrinter.getInstanceObject();
                    print.errorPrinter("Provided wrong dbName while performing select query by " + GlobalSessionDetails.loggedInUsername+ " on the database "+dbName);
                }

                if (!dbName.isEmpty() && !tableName.isEmpty()) {

                    // checking database and table existence added
                    if (DatabaseExists.validateDatabaseExistence(dbName) && TableExistence.checkIfTableExists(dbName, tableName)) {
                        columnInQuery = columnsMatcher.group(1).split(",");
                        List<String> totalColumn = Arrays.asList(readColumnsOfTable(dbName, tableName)); // total columns present in Table

                        if (!columnInQuery[0].trim().equals("*")) {
                            validateProvidedLegitColumns = validateIfColumnIsInTable(columnInQuery, totalColumn);
                        }

                        // looping to print the fetched records
                        if (validateProvidedLegitColumns) {

                            if (whereMatcher.find()) {
                                whereColumnName = whereMatcher.group(0).trim().split("=")[0];
                                whereColumnValue = whereMatcher.group(0).trim().split("=")[1].trim();
                            }

                            if (isTransaction) {
                                String selectFilePath = GlobalSessionDetails.getLoggedInUsername()
                                        .concat("/" + dbName + "/" + tableName) + ".txt";
                                String permanentTablePath = GlobalSessionDetails.getLoggedInUsername()
                                        .concat("/" + dbName.substring(4) + "/" + tableName) + ".txt";
                                File permanentTable = new File(permanentTablePath);
                                if (permanentTable.exists()) {
                                    File tempTable = new File(selectFilePath);
                                    if (!compareFiles(permanentTable, tempTable)) {
                                        createDuplicateCopy(tempTable, permanentTable);
                                    }
                                }
                            }

                            // write a logic to read from the file
                            List<List<String>> tableRecords = readFileForSelect(dbName, tableName, columnInQuery, whereColumnName, whereColumnValue);
                            fmt.format("\n");
                            for (int i = 0; i < tableRecords.size(); i++) {
                                for (int j = 0; j < tableRecords.get(i).size(); j++) {
                                    fmt.format("%30s", tableRecords.get(i).get(j));
                                }
                                fmt.format("\n");
                            }
                            System.out.println(fmt);
                            result = 7;
                            LogPrinter print = LogPrinter.getInstanceObject();
                            print.queryMessagePrinter("Select query is executed by " + GlobalSessionDetails.loggedInUsername+ " on the database "+dbName);
                        } else {
                            result = 8;
                            System.out.println("Provided Columns does not exists in table");
                            LogPrinter print = LogPrinter.getInstanceObject();
                            print.errorPrinter("Provided wrong column name while performing select query by " + GlobalSessionDetails.loggedInUsername+ " on the database "+dbName);
                        }
                    }
                }

            } else {
                result = 8;
                System.out.println("Please provide valid select syntax");
                LogPrinter print = LogPrinter.getInstanceObject();
                print.errorPrinter("Provided wrong select syntax by user " + GlobalSessionDetails.loggedInUsername+ " on the database "+dbName);
            }
            return result;
        }


        public boolean validateIfColumnIsInTable (String[]columnInQuery, List < String > totalColumn){
            // to check if column provided by user matches with table columns in DB
            if (columnInQuery.length > 0) {  //columnInQuery.length > 1
                for (int i = 0; i < columnInQuery.length; i++) {
                    if (!totalColumn.contains(columnInQuery[i].trim())) {
                        return false;
                    }
                }
            }
            return true;
        }

        public List<List<String>> readFileForSelect (String dbName, String tableName, String[]columnsInQuery, String
        whereColumnName, String whereColumnValue) throws Exception {
            String path = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/" + tableName + ".txt";
            List<List<String>> tableRecords = new ArrayList<List<String>>();
            // String[]columnsInQuery= Arrays.asList();
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            int counter = 0;
            boolean filterRowFound = false;
            // reading table file line by lilne for select output
            while (line != null) {
                String[] splitSt = line.split("#");
                List<String> rowValue = new ArrayList<String>();

                if (!filterRowFound) {
                    for (int i = 0; i < splitSt.length; i++) {
                        String[] columnNameValueSeparator = splitSt[i].trim().split(":");
                        if (columnNameValueSeparator.length > 0) {

                            // if columnNames are provided or
                            if (!columnsInQuery[0].trim().equals("*")) {
                                //if(whereColumnName.isEmpty()){
                                for (int ci = 0; ci < columnsInQuery.length; ci++) {
                                    if (whereColumnName.isEmpty()) {
                                        if (columnsInQuery[ci].trim().equals(columnNameValueSeparator[0])) {
                                            if (counter == 0) {
                                                fmt.format("%30s", columnNameValueSeparator[0]);
                                            }
                                            rowValue.add(columnNameValueSeparator[1]);
                                        }
                                    } else {
                                        if (line.contains(whereColumnName + ":" + whereColumnValue) && (columnsInQuery[ci].trim().equals(columnNameValueSeparator[0]))) {
                                            if (counter == 0) {
                                                fmt.format("%30s", columnNameValueSeparator[0]);
                                            }
                                            rowValue.add(columnNameValueSeparator[1]);
                                        }
                                    }

                                }
                                //}
                            /*else{
                                if (counter == 0) {
                                    fmt.format("%30s", columnNameValueSeparator[0]);
                                }
                                if(line.contains(whereColumnName+":"+whereColumnValue)) {
                                    rowValue.add(columnNameValueSeparator[1]);
                                }
                            }*/


                            } else {  //user want to fetch all (*) records
                                // if requested for all records
                                if (counter == 0) {
                                    // adding here output heading which will be column name
                                    fmt.format("%30s", columnNameValueSeparator[0]);
                                }
                                if (whereColumnName.isEmpty()) {
                                    rowValue.add(columnNameValueSeparator[1]);
                                } else {
                                    if (whereColumnName.equals(columnNameValueSeparator[0]) && whereColumnValue.equals(columnNameValueSeparator[1]) || filterRowFound) {

                                        filterRowFound = true;
                                        rowValue.add(columnNameValueSeparator[1]);
                                    }
                                }
                            }
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


        public String[] readColumnsOfTable (String dbName, String tableName) throws Exception {
            String tablePath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/schemaDetails.txt";
            List<String> columnNamesList = new ArrayList<String>();
            boolean fetchTableName = false;
            BufferedReader reader = new BufferedReader(new FileReader(tablePath));
            String line = reader.readLine();
            int counter = 0;
            while (line != null) {
                if (fetchTableName) {
                    columnNamesList = Arrays.asList(line.split("#"));
                    fetchTableName = false;
                    break;
                }
                if (line.contains("[" + tableName + "]")) {
                    fetchTableName = true;
                }

                line = reader.readLine();
            }
            reader.close();
            String[] columnName = new String[columnNamesList.size()];
            if (columnNamesList.size() > 0) {
                for (int i = 0; i < columnNamesList.size(); i++) {
                    columnName[i] = columnNamesList.get(i).split(":")[0];
                }
            }

            return columnName;
        }

        String readPrimaryKey (String dbName, String tableName) throws IOException {
            String primaryKey = "";
            String tablePath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/schemaDetails.txt";

            /*List<String> columnNamesList = new ArrayList<String>();*/
            int counter = 0;
            BufferedReader reader = new BufferedReader(new FileReader(tablePath));
            String line = reader.readLine();

            while (line != null) {
                if (counter == 2) {
                    primaryKey = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                    break;
                }
                if (line.contains("[" + tableName + "]") || counter == 1) {
                    counter = counter + 1;
                }

                line = reader.readLine();
            }
            reader.close();
            return primaryKey;
        }


        @Override
        public int updateATableRecords(String query, Boolean isTransaction) throws Exception {
            int result = 0;
            String dbName = "";
            String tableName = "";
            String columnInQuery;
            String whereColumnName = "";
            String whereColumnValue = "";
            String columnNewValue = "";
            String tmpFilePath = "";
            String updateTablePath = "";
            String oldValue = "";
            boolean validateProvidedLegitColumns = false;

            // Pattern to extract schema and table name
            Pattern patternDBTable = Pattern.compile("(?<=update)(.*)(?=set)", Pattern.CASE_INSENSITIVE);
            Matcher matcherDBTable = patternDBTable.matcher(query);

            // to extract column and column new value name
            Pattern columnsPattern = Pattern.compile("(?<=set\\s).*(?=\\swhere)", Pattern.CASE_INSENSITIVE);
            Matcher columnsMatcher = columnsPattern.matcher(query);

            // to extract where condition
            Pattern wherePattern = Pattern.compile("(?<=where\\s).*", Pattern.CASE_INSENSITIVE);
            Matcher whereMatcher = wherePattern.matcher(query);
            if (matcherDBTable.find()) {
                String[] separateDbtableName = matcherDBTable.group(0).split("\\.");

                // to check if user entered db and table name in query. if not then check if
                // global session contains value, if not then SOp, db absent
                if (separateDbtableName.length == 2) {
                    dbName = isTransaction ? "temp" + separateDbtableName[0].trim() : separateDbtableName[0].trim();
                    tableName = separateDbtableName[1].trim().toLowerCase();
                    //System.out.println(dbName);
                    //System.out.println(tableName);
                    LogPrinter print = LogPrinter.getInstanceObject();
                    print.queryMessagePrinter("Update query has been successfully executed by user " + GlobalSessionDetails.loggedInUsername+ " on the database "+ dbName);
                    print.generalMessagePrinter("The database "+ dbName + " created by " + GlobalSessionDetails.loggedInUsername+ " has " ,"update",dbName);

                } else if (!GlobalSessionDetails.getDbInAction().isEmpty()) {
                    dbName = GlobalSessionDetails.getDbInAction().trim();
                    tableName = matcherDBTable.group(0).trim().toLowerCase();
                } else {
                    result = 10;
                    System.out.println("Either provide dbName or use useDB operation");
                    LogPrinter print = LogPrinter.getInstanceObject();
                    print.errorPrinter("Provided database by user " + GlobalSessionDetails.loggedInUsername + " doesn't exist.");
                }

                if (!tableName.isEmpty() && !dbName.isEmpty()) {

                    // check if db and table exists
                    if (DatabaseExists.validateDatabaseExistence(dbName)
                            && TableExistence.checkIfTableExists(dbName, tableName)) {

                        // set column value
                        if (columnsMatcher.find()) {
                            columnInQuery = columnsMatcher.group(0).trim().split("=")[0];
                            columnNewValue = columnsMatcher.group(0).trim().split("=")[1].trim();
                            // String[] validateColProvidedArray=[columnInQuery];
                            List<String> totalColumn = Arrays.asList(readColumnsOfTable(dbName, tableName));// total
                            // columns
                            // present
                            // in Table
                            validateProvidedLegitColumns = validateIfColumnIsInTable(new String[]{columnInQuery},
                                    totalColumn);
                            if (whereMatcher.find() && validateProvidedLegitColumns) {
                                whereColumnName = whereMatcher.group(0).trim().split("=")[0];
                                whereColumnValue = whereMatcher.group(0).trim().split("=")[1].trim();

                                updateTablePath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/"
                                        + tableName + ".txt";
                                File updateFile = new File(updateTablePath);

                                if (isTransaction) {
                                    String permanentTablePath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/"
                                            + tableName.substring(4) + ".txt";
                                    File permanentFile = new File(permanentTablePath);
                                    if (permanentFile.exists()) {
                                        if (!compareFiles(permanentFile, updateFile)) {
                                            createDuplicateCopy(updateFile, permanentFile);
                                        }
                                    }
                                }

                                tmpFilePath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/" + tableName
                                        + "_tmp.txt";
                                // creating a new file to write everything into it with updated value
                                BufferedReader br = new BufferedReader(new FileReader(updateTablePath));
                                BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFilePath));

                                String line = br.readLine();
                                while (line != null) {
                                    StringBuilder newLine = new StringBuilder();
                                    String[] splitSt = line.split("#");
                                    boolean filterRowFound = false;
                                    for (int i = 0; i < splitSt.length; i++) {
                                        String[] columnNameValueSeparator = splitSt[i].trim().split(":");

                                        if (columnNameValueSeparator.length > 0) {
                                            if (whereColumnName.trim().equals(columnNameValueSeparator[0])
                                                    && whereColumnValue.trim().equals(columnNameValueSeparator[1])) {
                                                filterRowFound = true;
                                                oldValue = columnNameValueSeparator[1]; // loggers if want to display value
                                                // changed while updating
                                            }
                                            if (filterRowFound
                                                    && columnInQuery.trim().equals(columnNameValueSeparator[0])) {
                                                newLine.append(columnNameValueSeparator[0] + ":" + columnNewValue + "#");
                                            } else {
                                                newLine.append(splitSt[i] + "#");
                                            }
                                        }

                                        // System.out.println("newLine.substring(0,newLine.length()-1) "+
                                        // newLine.substring(0,newLine.length()-1));

                                        // newLine.setLength(0);
                                    }
                                    bw.append(newLine.substring(0, newLine.length() - 1));
                                    bw.newLine();
                                    line = br.readLine();
                                }
                                bw.close();
                                br.close();

                                // delete the actual data file and renaming the temp file to actual file
                                updateFile.delete();
                                File newfile = new File(tmpFilePath);
                                newfile.renameTo(updateFile);
                            }
                        }

                    }
                    result = 9;
                    CreateStructureAndDataExportFile.insertInStructureAndDataExportFile(query, dbName);
                    Analy.update(GlobalSessionDetails.getDbInAction(), 1, tableName);
                }
            } else {
                result = 10;
                System.out.println("Please enter valid update syntax");
                LogPrinter print = LogPrinter.getInstanceObject();
                print.errorPrinter("Provided wrong update syntax by user " + GlobalSessionDetails.loggedInUsername+ " while performing operation on the database "+dbName);
            }
            return result;

        }

        /*
         * DELETE FROM table_name WHERE condition; DELETE FROM university.user WHERE
         * id=1;
         */
        @Override
        public int deleteATableRecords(String query, Boolean isTransaction) throws Exception {
            int result = 0;
            String dbName = "";
            String tableName = "";
            String whereColumnName = "";
            String whereColumnValue = "";
            String tmpFilePath = "";
            String deleteTablePath = "";
            boolean validateProvidedLegitColumns = false;

            // Pattern to extract schema and table name
            Pattern patternDBTable = Pattern.compile("(?<=from)(.*)(?=where)", Pattern.CASE_INSENSITIVE);
            Matcher matcherDBTable = patternDBTable.matcher(query);

            // to extract where condition
            Pattern wherePattern = Pattern.compile("(?<=where\\s).*", Pattern.CASE_INSENSITIVE);
            Matcher whereMatcher = wherePattern.matcher(query);

            /*
             * if(matcherDBTable.find()){
             * System.out.println("Group 0"+matcherDBTable.group(0)+"Group 1"+matcherDBTable
             * .group(1));
             * System.out.println(whereMatcher.find()+"Group 0 "+whereMatcher.group(0)); }
             */

            if (matcherDBTable.find()) {
                String[] separateDbtableName = matcherDBTable.group(0).split("\\.");

                // to check if user entered bdb and table name in query. if not then check if
                // global session contains value, if not then SOp, db absent
                if (separateDbtableName.length == 2) {
                    dbName = isTransaction ? "temp" + separateDbtableName[0].trim() : separateDbtableName[0].trim();
                    tableName = separateDbtableName[1].trim().toLowerCase();
                    LogPrinter print = LogPrinter.getInstanceObject();
                    print.queryMessagePrinter("Successfully deleted a row in the table " +tableName + " on the database " +dbName+ " given by user "+GlobalSessionDetails.loggedInUsername);
                    print.generalMessagePrinter("The database "+ dbName + " created by " + GlobalSessionDetails.loggedInUsername+ " has " ,"delete",dbName);

                } else if (!GlobalSessionDetails.getDbInAction().isEmpty()) {
                    dbName = GlobalSessionDetails.getDbInAction().trim();
                    tableName = matcherDBTable.group(0).trim().toLowerCase();
                } else {
                    result = 12;
                    System.out.println("Either provide dbName or use useDB operation");
                    LogPrinter print = LogPrinter.getInstanceObject();
                    print.errorPrinter("Provided wrong dbname by user " + GlobalSessionDetails.loggedInUsername+ " while performing delete table operation on the database "+dbName);
                }
                }

                if (!tableName.isEmpty() && !dbName.isEmpty()) {

                    // check if db and table exists
                    if (DatabaseExists.validateDatabaseExistence(dbName)
                            && TableExistence.checkIfTableExists(dbName, tableName)) {

                        // List<String> totalColumn = Arrays.asList(readColumnsOfTable(dbName,
                        // tableName)); // total columns present in Table

                        if (whereMatcher.find()) {
                            whereColumnName = whereMatcher.group(0).trim().split("=")[0];
                            whereColumnValue = whereMatcher.group(0).trim().split("=")[1].trim();

                            deleteTablePath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/" + tableName
                                    + ".txt";
                            System.out.println(deleteTablePath);
                            tmpFilePath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/" + tableName
                                    + "_tmp.txt";

                            File deleteOldTableFile = new File(deleteTablePath);
                            if (isTransaction) {
                                String permanentTablePath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/"
                                        + tableName.substring(4) + ".txt";
                                File permanentFile = new File(permanentTablePath);
                                if (permanentFile.exists()) {
                                    if (!compareFiles(permanentFile, deleteOldTableFile)) {
                                        System.out.println("Creating a copy line 670");
                                        createDuplicateCopy(deleteOldTableFile, permanentFile);
                                    }
                                }
                            }

                            // creating a new file to write everything into it with deleted value
                            BufferedReader br = new BufferedReader(new FileReader(deleteTablePath));
                            BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFilePath));

                            String line = br.readLine();
                            while (line != null) {
                                // boolean containsWord=
                                if (line.contains(whereColumnName + ":" + whereColumnValue)) {
                                    line = br.readLine();
                                    continue;
                                } else {
                                    bw.append(line);
                                    bw.newLine();
                                }
                                /*
                                 * bw.append(newLine.substring(0,newLine.length()-1)); bw.newLine();
                                 */
                                line = br.readLine();
                            }

                            bw.close();
                            br.close();

                            deleteOldTableFile.delete();
                            File newfile = new File(tmpFilePath);
                            newfile.renameTo(deleteOldTableFile);

                            result = 11;
                            CreateStructureAndDataExportFile.insertInStructureAndDataExportFile(query, dbName);
                            Analy.delete(GlobalSessionDetails.getDbInAction(), 1, tableName);


                            /*
                             * filePath.delete(); File newfile = new File(tempFilePath);
                             * newfile.renameTo(filePath);
                             */
                        }
                        // }

                    }

                }
            else {
                result = 12;
                System.out.println("Please enter valid update syntax");
                LogPrinter print = LogPrinter.getInstanceObject();
                print.errorPrinter("Provided wrong delete syntax by user " + GlobalSessionDetails.loggedInUsername+ " while performing delete operation on the database "+dbName);
            }
            return result;
        }

        // DROP TABLE table_name;
        // Drop table university.user;
        @Override
        public int deleteTable(String query, Boolean isTransaction) throws IOException {
            int result = 0;
            String dbName = "";
            String tableName = "";
            String deleteTablePath = "";
            String schemaFilePath = "";
            String dataAndStructureFilePath = "";
            String tmpSchemaFilePath = "";
            String tmpDataAndExportFilePath = "";

            Pattern dbTablePattern = Pattern.compile(".*drop\\s+table\\s+(.*?)($|\\s+)", Pattern.CASE_INSENSITIVE);
            Matcher matcherDBTable = dbTablePattern.matcher(query);

            if (matcherDBTable.find()) {
                String[] separateDbtableName = matcherDBTable.group(1).split("\\.");

                // to check if user entered bdb and table name in query. if not then check if global session contains value, if not then SOp, db absent
                if (separateDbtableName.length == 2) {
                    dbName = isTransaction ? "temp" + separateDbtableName[0].trim() : separateDbtableName[0].trim();
                    tableName = separateDbtableName[1].trim().toLowerCase();

                } else if (!GlobalSessionDetails.getDbInAction().isEmpty()) {
                    dbName = GlobalSessionDetails.getDbInAction().trim();
                    tableName = matcherDBTable.group(0).trim().toLowerCase();
                } else {
                    result = 14;
                    System.out.println("Either provide dbName or use useDB operation");
                }
                if (!tableName.isEmpty() && !dbName.isEmpty()) {
                    // check if db and table exists
                    if (DatabaseExists.validateDatabaseExistence(dbName) && TableExistence.checkIfTableExists(dbName, tableName)) {

                        deleteTablePath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/" + tableName + ".txt";
                        schemaFilePath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/" + "schemaDetails.txt";
                        tmpSchemaFilePath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/" + "schemaDetails_tmp.txt";

                        /*Schema.txt file update*/
                        // creating instance of schema file to update with deleted content
                        File deleteOldSchemaFile = new File(schemaFilePath);

                        // creating a new schema file to update it by deleting table details in it
                        BufferedReader bufferSchemaReader = new BufferedReader(new FileReader(schemaFilePath));
                        BufferedWriter bufferSchemaWriter = new BufferedWriter(new FileWriter(tmpSchemaFilePath));
                        int schemaCounter = 0;
                        String schemaLine = bufferSchemaReader.readLine();
                        while (schemaLine != null) {
                            if (schemaLine.contains("[" + tableName + "]") || schemaCounter == 1) {
                                if (schemaCounter == 1) {
                                    schemaCounter = 2;
                                } else {
                                    schemaCounter = 1;
                                }
                                schemaLine = bufferSchemaReader.readLine();
                                continue;
                            } else {
                                bufferSchemaWriter.append(schemaLine);
                                bufferSchemaWriter.newLine();
                            }
                            schemaLine = bufferSchemaReader.readLine();
                        }
                        bufferSchemaWriter.close();
                        bufferSchemaReader.close();
                        deleteOldSchemaFile.delete();   // deleting old schema text file
                        File newSchemfile = new File(tmpSchemaFilePath);
                        newSchemfile.renameTo(deleteOldSchemaFile);
                        /* Updating strctureAndExport txt File */
                        // creating a new schema file to update it by deleting table details in it
                        dataAndStructureFilePath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/" + "structureAndDataExport.txt";
                        tmpDataAndExportFilePath = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/" + "structureAndDataExport_tmp.txt";
                        File deleteOldStructureAndOldFile = new File(dataAndStructureFilePath);

                        BufferedReader bufferDataAndExportReader = new BufferedReader(new FileReader(dataAndStructureFilePath));
                        BufferedWriter bufferDataAndExportWriter = new BufferedWriter(new FileWriter(tmpDataAndExportFilePath));

                        String dataAndExportlineReader = bufferDataAndExportReader.readLine();

                        while (dataAndExportlineReader != null) {
                            if (dataAndExportlineReader.contains(dbName + "." + tableName)) {
                                dataAndExportlineReader = bufferDataAndExportReader.readLine();
                                continue;
                            } else {
                                bufferDataAndExportWriter.append(dataAndExportlineReader);
                                bufferDataAndExportWriter.newLine();
                                dataAndExportlineReader = bufferDataAndExportReader.readLine();
                            }
                        }
                        bufferDataAndExportWriter.close();
                        bufferDataAndExportReader.close();
                        // renaming structcureAndExport_tmp as old value.
                        deleteOldStructureAndOldFile.delete(); // delete old structureAndExport txtx file
                        File newStructurefile = new File(tmpDataAndExportFilePath);
                        newStructurefile.renameTo(deleteOldStructureAndOldFile);
                        File deleteOldTableFile = new File(deleteTablePath);
                        deleteOldTableFile.delete();    // deleting table file
                        result = 13;
                    }
                }
            } else {
                result = 14;
            }
            return result;
        }

        @Override
        public int useDb(String query, Boolean isTransaction) throws IOException {
            int result = 0;
            String dbName = "";
            if (!query.isEmpty() && query.toLowerCase().contains("use")) {
                String subQuery = query.replace("use", "").trim();
                //String dbname = QueryOperations.removeSemiColon(subQuery);
                if (!subQuery.isEmpty()) {
                    dbName = isTransaction ? "temp" + subQuery : subQuery;
                    if (DatabaseExists.validateDatabaseExistence(dbName)) {
                        GlobalSessionDetails.setDbInAction(dbName);
                        result = 15;
                    } else {
                        result = 16;
                    }
                    // System.out.println("Use "+dbName+" applied");
                }
            } else {
                result = 16;
            }
            return result;
        }

        public boolean validatePrimaryKey (String primaryKey, String[]columns){
            boolean validatedPrimaryKey = false;
            String actualPrimaryKey = primaryKey.substring(primaryKey.indexOf("(") + 1, primaryKey.indexOf(")"));
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].trim().toLowerCase().equals(actualPrimaryKey.trim().toLowerCase())) {
                    validatedPrimaryKey = true;
                }
            }
            return validatedPrimaryKey;
        }

        int getPrimaryKeyIndex (String primaryKey, String[]columns){
            int index = -1;
            for (int i = 0; i < columns.length; i++) {
                if (primaryKey.equals(columns[i])) {
                    index = i;
                }
            }

            return index;
        }

        boolean duplicatePrimaryKey (String dbName, String tableName, String primaryKeyDuplicateValue) throws Exception
        {
            boolean duplicatePrimaryKey = false;

            String path = GlobalSessionDetails.getLoggedInUsername() + "/" + dbName + "/" + tableName + ".txt";
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();

            while (line != null) {
                if (line.contains(primaryKeyDuplicateValue)) {
                    duplicatePrimaryKey = true;
                    break;
                }
                line = reader.readLine();
            }
            reader.close();
            return duplicatePrimaryKey;
        }
    }


