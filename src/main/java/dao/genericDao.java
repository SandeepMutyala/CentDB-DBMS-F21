package dao;

import java.io.Serializable;

public interface genericDao<T, PK extends Serializable> {
	//generic append function which takes care of appending data to the text file
		public void append(T newInstance); 
	//generic function for reading a text file
		public void readfile(String filename);
}
