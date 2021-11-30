package dao;

import model.User;

import java.util.HashMap;

public interface userDao extends genericDao<User, Integer> {
	public void append(User user);
	public HashMap<String, Object> readfile(String filename);
}
