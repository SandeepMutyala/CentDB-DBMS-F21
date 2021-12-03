package dao;

import model.User;

import java.util.HashMap;

public interface DatabaseOperations{
    int createDb(String query);
    void useDb();
    int createTable(String query);
    void insertInTable(String query);
    void fetchTableRecords();
    void updateATableRecords();
    void deleteATableRecords();
    void deleteTable();
}
