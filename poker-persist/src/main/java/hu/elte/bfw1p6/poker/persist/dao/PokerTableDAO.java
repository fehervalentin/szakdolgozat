package hu.elte.bfw1p6.poker.persist.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;

/**
 * A póker játéktáblák letárolásáért felelős osztály.
 * @author feher
 *
 */
public class PokerTableDAO extends GenericDAO<PokerTable> {

	public PokerTableDAO() throws PokerDataBaseException {
		super(PokerTable.TABLE_NAME);
	}

	@Override
	public synchronized List<PokerTable> findAll() throws PokerDataBaseException {
		try(Statement stmt = dbManager.getConnection().createStatement()) {
			List<PokerTable> tables = new ArrayList<>();
			ResultSet rs = stmt.executeQuery(FIND_ALL);
			while (rs.next()) {
				PokerTable t = new PokerTable();
				for (int i = 0; i < columns.length; i++) {
					t.set(i, rs.getObject(columns[i]));
				}
				t.setId(rs.getInt(ID_COLUMN));
				tables.add(t);
			}
			return tables;
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
	}
}