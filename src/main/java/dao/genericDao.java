package dao;

import java.io.Serializable;
import java.util.HashMap;

public interface genericDao<T, PK extends Serializable> {
	//generic append function which takes care of appending data to the text file
		void append(T newInstance);
	//generic function for reading a text file
		HashMap<String, Object> readfile(String filename);
}
