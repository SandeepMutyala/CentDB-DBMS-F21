package dao;

import model.User;

import java.io.IOException;
import java.util.HashMap;

public interface DatabaseOperations{
    int createDb(String query);
    void useDb();
    int createTable(String query);
    int insertInTable(String query) throws IOException;
    int fetchTableRecords(String query) throws Exception;
    int updateATableRecords(String query) throws Exception;
    void deleteATableRecords();
    void deleteTable();
}
