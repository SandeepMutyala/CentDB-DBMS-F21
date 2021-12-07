package dao;

import model.User;

import java.io.IOException;
import java.util.HashMap;

public interface DatabaseOperations{
    int createDb(String query, Boolean isTransaction) throws IOException;
    void useDb();
    int createTable(String query, Boolean isTransaction);
    int insertInTable(String query, Boolean isTransaction) throws IOException;
    int fetchTableRecords(String query, Boolean isTransaction) throws Exception;
    int updateATableRecords(String query,Boolean isTransaction) throws Exception;
    int deleteATableRecords(String query, Boolean isTransaction) throws Exception;
    void deleteTable();
}
