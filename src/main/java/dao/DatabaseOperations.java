package dao;

import model.User;

import java.io.IOException;
import java.util.HashMap;

public interface DatabaseOperations{
    int createDb(String query, Boolean isTransaction) throws IOException;
    void useDb();
    int createTable(String query, Boolean isTransaction);
    int insertInTable(String query, Boolean isTransaction) throws IOException;
    void fetchTableRecords();
    void updateATableRecords();
    void deleteATableRecords();
    void deleteTable();
}
