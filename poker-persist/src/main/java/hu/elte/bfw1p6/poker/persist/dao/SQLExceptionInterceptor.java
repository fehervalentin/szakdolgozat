package hu.elte.bfw1p6.poker.persist.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import hu.elte.bfw1p6.poker.exception.database.PokerDataBaseException;

//import hu.elte.bfw1p6.poker.exception.database.PokerDataBaseException;

public class SQLExceptionInterceptor {
	private static SQLExceptionInterceptor instance = null;
	
	private final int IGNORE_ERR_CODE = 45000;
	
	private HashMap<String, String> mappings;

	private SQLExceptionInterceptor() {
		mappings = new HashMap<>();
		fillMapper();
	}

	public static synchronized SQLExceptionInterceptor getInstance() {
		if (instance == null) {
			instance = new SQLExceptionInterceptor();
		}
		return instance;
	}
	
	private void fillMapper() {
		mappings.put("UQ_users_username", "Ilyen felhasználónévvel már regisztráltak!");
	}
	
	public PokerDataBaseException interceptException(SQLException ex) {
		if (ex.getErrorCode() == IGNORE_ERR_CODE) {
			return new PokerDataBaseException(ex.getMessage());
		}
		String sqlMsg = ex.getMessage();
		String pokerMsg = lookUpMsg(sqlMsg);
		return new PokerDataBaseException(pokerMsg);
	}
	
	private String lookUpMsg(String msg) {
		Iterator<String> iter = mappings.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (msg.contains(key)) {
				return mappings.get(key);
			}
		}
		return msg;
	}
}
