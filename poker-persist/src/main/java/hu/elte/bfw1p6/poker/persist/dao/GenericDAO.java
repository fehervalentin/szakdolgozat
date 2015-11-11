package hu.elte.bfw1p6.poker.persist.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.model.entity.EntityWithId;
import hu.elte.bfw1p6.poker.persist.helper.DBManager;
import hu.elte.bfw1p6.poker.persist.helper.SQLExceptionTranslator;

/**
 * A póker játék táblák letárolásáért felelős generikus osztály.
 * @author feher
 *
 */
public abstract class GenericDAO<T extends EntityWithId> {
	
	/**
	 * Az adatbázisban letárolt tábla neve.
	 */
	protected final String TABLE_NAME;
	
	/**
	 * Az adatbázis táblában található oszlopok nevei.
	 */
	protected String[] columns;

	protected String FIND_ALL;
	private String INSERT;
	private String UPDATE;
	protected String DELETE;
	
	protected DBManager dbManager;

	protected SQLExceptionTranslator sqlExceptionTranslator;

	public GenericDAO(String tableName) throws PokerDataBaseException {
		this.TABLE_NAME = tableName;
		this.sqlExceptionTranslator = new SQLExceptionTranslator();
		this.dbManager = new DBManager();
		FIND_ALL = "SELECT * FROM " + TABLE_NAME + ";";
		DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id=?;";
		loadColumns();
	}

	/**
	 * Entitás ment az adatbázisba.
	 * @param t a letárolandó entitás
	 * @throws PokerDataBaseException
	 */
	public synchronized void save(T t) throws PokerDataBaseException {
		try {
			Connection con = dbManager.getConnection();
			PreparedStatement pstmt = con.prepareStatement(INSERT);
			for (int i = 0; i < columns.length; i++) {
				pstmt.setObject(i+1, t.get(i));
			}
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
	}

	/**
	 * Lekéri az összes (meghatározott típusú) entitást az adatbázisból.
	 * @return az összes entitás
	 * @throws PokerDataBaseException
	 */
	public abstract List<T> findAll() throws PokerDataBaseException;
	
	/**
	 * Létezó entitást töröl az adatbázisból.
	 * @param t a törlendő entitás
	 * @throws PokerDataBaseException
	 */
	public synchronized void delete(T t) throws PokerDataBaseException {
		System.out.println("Delete:" + DELETE);
		try {
			Connection con = dbManager.getConnection();
			PreparedStatement pstmt = con.prepareStatement(DELETE);
			pstmt.setInt(1, t.getId());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
	}

	/**
	 * Létező entitást módosít.
	 * @param t a módosítandó entitás
	 * @throws PokerDataBaseException
	 */
	public synchronized void modify(T t) throws PokerDataBaseException {
		try {
			Connection con = dbManager.getConnection();
			PreparedStatement pstmt = con.prepareStatement(UPDATE);
			for (int i = 0; i < columns.length; i++) {
				pstmt.setObject(i+1, t.get(i));
			}
			pstmt.setObject(columns.length + 1, t.getId());
			pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}

	}
	
	/**
	 * Egy próbaqueryvel lekérdezi az adatbázistáblában található oszlopok neveit.
	 * @throws PokerDataBaseException
	 */
	private void loadColumns() throws PokerDataBaseException {
		Connection con = dbManager.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(FIND_ALL);
			int count = rs.getMetaData().getColumnCount() - 1;
			columns = new String[count];
			for (int i = 0; i < count; i++) {
				columns[i] = rs.getMetaData().getColumnLabel(i + 2);
			}
			stmt.close();
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
		createQueries();
	}

	/**
	 * Az alapqueryket állítja elő.
	 * @throws PokerDataBaseException
	 */
	private void createQueries() throws PokerDataBaseException {
		INSERT = "INSERT INTO " + TABLE_NAME + "(" + String.join(",", columns) + ")" + "VALUES ("+ String.join(",", Collections.nCopies(columns.length, "?")) + ")";
		UPDATE = createQueryForUpdate();
	}

	/**
	 * Az UPDATE queryt állítja elő.
	 * @return
	 */
	private String createQueryForUpdate() {
		StringBuilder sb = new StringBuilder("UPDATE " + TABLE_NAME + " SET " + columns[0] + "=?");
		for (int i = 1; i < columns.length; i++) {
			sb.append("," + columns[i] + "=?");
		}
		sb.append(" WHERE id=?;");
		return sb.toString();
	}
}	