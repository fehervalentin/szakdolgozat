package hu.elte.bfw1p6.poker.persist.helper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;

/**
 * Az adatbázisból érkező egyedi megszorítások megsértéséből fakadó hibákat fordítja át.
 * @author feher
 *
 */
public class SQLExceptionTranslator {
	
	/**
	 * Az adatbázisból jövő egyéni megszorításokat és az azokhoz tartozó nyelvi átfordításokat tartalmazza.
	 */
	private HashMap<String, String> mappings;

	public SQLExceptionTranslator() {
		mappings = new HashMap<>();
		fillMapper();
	}
	
	/**
	 * Feltölti a mappert.
	 */
	private void fillMapper() {
		mappings.put("UQ_users_username", "Ilyen felhasználónévvel már regisztráltak!");
		mappings.put("CONSTRAINT_USERS_USERNAME_LENGHT", "A felhasznalonev hossza nem esik bele a [3-20] intervallumba!");
		mappings.put("CONSTRAINT_USERS_PASSWORD_LENGHT", "Szerver hiba!"); // valószínúleg hash fv hiba lenne/lesz...
		
		mappings.put("UQ_poker_table_name", "Ilyen nevű asztal már létezik!");
		mappings.put("CONSTRAINT_POKER_TABLES_MAX_TIME", "A gondolkodasi ido nem esik bele a [5-40] intervallumba!");
		mappings.put("CONSTRAINT_POKER_TABLES_MAX_PLAYERS", "A jatekosok szama nem esik bele a [2-6] intervallumba!");
		mappings.put("CONSTRAINT_POKER_TABLES_NAME_LENGHT", "A szerver neve túl hosszú!");
		
		mappings.put("UQ_POKER_TYPES_NAME", "Ilyen nevű játéktípus már létezik az adatbázisban!");
		
		mappings.put("Data truncation", "Érték hiba! A dokumentáció alapján ellenőrizze a beviteli értékeket");
	}
	
	/**
	 * Átfordítja az SQLException paramétert.
	 * @param e az átofrdítandó SQLException objektum
	 * @return az átfordított kivétel
	 */
	public PokerDataBaseException interceptException(SQLException e) {
		String pokerMsg = lookUpMsg(e.getMessage());
		return new PokerDataBaseException(pokerMsg);
	}
	
	/**
	 * Megkeresi a mapperben, hogy át tudja-e fordítani az adatbázisból érkező egyéni megszorítások neveit,
	 * ha nem, akkor az eredeti üzenetet továbbítja,
	 * ha igen, akkor az átfordított üzenetet küldi tovább.
	 * @param msg az sql hiba leírása
	 * @return az átfordított üzenet
	 */
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