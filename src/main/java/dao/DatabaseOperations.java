package dao;

import model.User;

import java.io.IOException;
import java.util.HashMap;

public interface DatabaseOperations{
    int createDb(String query);
    void useDb();
    int createTable(String query);
    void insertInTable(String query) throws IOException;
    void fetchTableRecords();
    void updateATableRecords();
    void deleteATableRecords();
    void deleteTable();
}
