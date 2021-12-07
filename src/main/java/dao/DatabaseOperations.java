package dao;

import model.User;

import java.io.IOException;
import java.util.HashMap;

public interface DatabaseOperations{
    int createDb(String query);
    int createTable(String query);
    int insertInTable(String query) throws Exception;
    int fetchTableRecords(String query) throws Exception;
    int updateATableRecords(String query) throws Exception;
    int deleteATableRecords(String query) throws Exception;
    int deleteTable(String query) throws IOException;
    int useDb(String query) throws IOException;
}
