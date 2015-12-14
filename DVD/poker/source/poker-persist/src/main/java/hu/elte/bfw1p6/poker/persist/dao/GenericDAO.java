package hu.elte.bfw1p6.poker.persist.dao;

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
 * @param <T> Az entitás típusa.
 */
public abstract class GenericDAO<T extends EntityWithId> {
	
	/**
	 * Az adatbázisban letárolt tábla neve.
	 */
	protected final String TABLE_NAME;
	
	/**
	 * Az adatbázistáblákban az elsődleges kulcs oszlop neve.
	 */
	protected final String ID_COLUMN = "id";
	
	/**
	 * Az adatbázis táblában található oszlopok nevei.
	 */
	protected String[] columns;

	protected String FIND_ALL;
	protected String DELETE;
	private String INSERT;
	private String UPDATE;
	
	/**
	 * Az adatbázissal való kapcsolatot felügyelő osztály.
	 */
	protected DBManager dbManager;

	/**
	 * Az adatbázisból érkező kivételeket fordítja át.
	 */
	protected SQLExceptionTranslator sqlExceptionTranslator;

	public GenericDAO(String tableName) throws PokerDataBaseException {
		this.TABLE_NAME = tableName;
		this.sqlExceptionTranslator = new SQLExceptionTranslator();
		this.dbManager = new DBManager();
		FIND_ALL = "SELECT * FROM " + TABLE_NAME + ";";
		loadColumns();
		DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id=?;";
		INSERT = "INSERT INTO " + TABLE_NAME + "(" + String.join(",", columns) + ")" + "VALUES ("+ String.join(",", Collections.nCopies(columns.length, "?")) + ");";
		UPDATE = "UPDATE " + TABLE_NAME + " SET " + String.join("=?, ", columns) + "=? WHERE id=?;";
	}

	/**
	 * Entitást ment az adatbázisba.
	 * @param t a letárolandó entitás
	 * @throws PokerDataBaseException
	 */
	public synchronized void save(T t) throws PokerDataBaseException {
		try(PreparedStatement pstmt = dbManager.getConnection().prepareStatement(INSERT)) {
			for (int i = 0; i < columns.length; i++) {
				pstmt.setObject(i+1, t.get(i));
			}
			pstmt.executeUpdate();
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
		try(PreparedStatement pstmt = dbManager.getConnection().prepareStatement(DELETE)) {
			pstmt.setInt(1, t.getId());
			pstmt.executeUpdate();
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
		try(PreparedStatement pstmt = dbManager.getConnection().prepareStatement(UPDATE)) {
			for (int i = 0; i < columns.length; i++) {
				pstmt.setObject(i+1, t.get(i));
			}
			pstmt.setObject(columns.length + 1, t.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
	}
	
	/**
	 * Egy próbaqueryvel lekérdezi az adatbázistáblában található oszlopok neveit.
	 * @throws PokerDataBaseException
	 */
	private void loadColumns() throws PokerDataBaseException {
		try(Statement stmt = dbManager.getConnection().createStatement()) {
			ResultSet rs = stmt.executeQuery(FIND_ALL);
			int count = rs.getMetaData().getColumnCount() - 1;
			columns = new String[count];
			for (int i = 0; i < count; i++) {
				columns[i] = rs.getMetaData().getColumnLabel(i + 2);
			}
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
	}
}	