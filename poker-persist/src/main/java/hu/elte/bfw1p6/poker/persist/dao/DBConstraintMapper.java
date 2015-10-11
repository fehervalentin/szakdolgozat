package hu.elte.bfw1p6.poker.persist.dao;

import java.sql.SQLException;
import java.util.HashMap;

public class DBConstraintMapper {
	private static DBConstraintMapper instance = null;
	
	private HashMap<String, String> mappings;

	private DBConstraintMapper() {
		mappings = new HashMap<>();
	}

	public static synchronized DBConstraintMapper getInstance() {
		if (instance == null) {
			instance = new DBConstraintMapper();
		}
		return instance;
	}
	
	private void fillMapper() {
		mappings.put("UQ_users_username", "Ilyen felhasználónévvel már regisztráltak!");
	}
	
	public PokerDataBaseException interceptException(SQLException ex) {
		
	}
}
