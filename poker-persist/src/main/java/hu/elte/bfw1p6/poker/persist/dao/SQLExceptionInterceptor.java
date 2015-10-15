package hu.elte.bfw1p6.poker.persist.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;

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
		mappings.put("CONSTRAINT_USERS_USERNAME_LENGHT", "A felhasznalonev hossza nem esik bele a [3-20] intervallumba!");
		mappings.put("CONSTRAINT_USERS_PASSWORD_LENGHT", "Szerver hiba!"); // valószínúleg hash fv hiba lenne/lesz...
		
		mappings.put("UQ_poker_table_name", "Ilyen nevű asztal már létezik!");
		mappings.put("CONSTRAINT_POKER_TABLES_MAX_TIME", "A gondolkodasi ido nem esik bele a [5-40] intervallumba!");
		mappings.put("CONSTRAINT_POKER_TABLES_MAX_PLAYERS", "A jatekosok szama nem esik bele a [2-6] intervallumba!");
		mappings.put("CONSTRAINT_POKER_TABLES_POT_HIGHER_THAN_MAX_BET", "Az alaptet nem lehet lehet nagyobb a limitnel!");
		mappings.put("CONSTRAINT_POKER_TABLES_NAME_LENGHT", "A szerver neve túl hosszú!");
		
		mappings.put("UQ_POKER_TYPES_NAME", "Ilyen nevű játéktípus már létezik az adatbázisban!");
	}
	
	public PokerDataBaseException interceptException(SQLException ex) {
		/*if (ex.getErrorCode() == IGNORE_ERR_CODE) {
			return new PokerDataBaseException(ex.getMessage());
		}*/
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
