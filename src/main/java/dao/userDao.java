package dao;

import model.User;

public interface userDao extends genericDao<User,Integer> {
	public void append(User user);
	
	public void readfile(String filename);
}
