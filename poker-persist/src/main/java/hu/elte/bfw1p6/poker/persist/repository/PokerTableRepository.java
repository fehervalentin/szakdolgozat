package hu.elte.bfw1p6.poker.persist.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.persist.dao.DBManager;
import hu.elte.bfw1p6.poker.persist.dao.SQLExceptionInterceptor;

public class PokerTableRepository {
	private final String TABLE_NAME = "poker_tables";

	private static String[] columns;
	private static PokerTableRepository instance = null;

	private String FIND_ALL = "SELECT * FROM " + TABLE_NAME + ";";
	private String INSERT;
	private String UPDATE;
	private String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id=?;";

	private SQLExceptionInterceptor interceptor = SQLExceptionInterceptor.getInstance();

	private PokerTableRepository() throws PokerDataBaseException {
		loadColumns();
	}

	public static synchronized PokerTableRepository getInstance() throws PokerDataBaseException {
		if (instance == null) {
			instance = new PokerTableRepository();
		}
		return instance;
	}

	public synchronized int save(PokerTable t) throws PokerDataBaseException {
		int iRet = -1;
		try {
			Connection con = DBManager.getInstance().getConnection();
			PreparedStatement pstmt = con.prepareStatement(INSERT);
			for (int i = 0; i < columns.length; i++) {
				Object valami = t.get(i);
				pstmt.setObject(i+1, valami);
			}
			iRet = pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException e) {
			throw interceptor.interceptException(e);
		}

		return iRet;
	}

	public synchronized List<PokerTable> findAll() throws PokerDataBaseException {
		List<PokerTable> tables = new ArrayList<>();

		try {
			String QRY = FIND_ALL;
			Connection con = DBManager.getInstance().getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(QRY);

			while (rs.next()) {
				PokerTable t = new PokerTable();
				for (int i = 0; i < columns.length; i++) {
					t.set(i, rs.getObject(columns[i]));
				}
				t.setId(rs.getInt("id"));
				tables.add(t);
			}

			stmt.close();
		} catch (SQLException e) {
			throw interceptor.interceptException(e);
		}
		return tables;
	}
	
	public synchronized void deleteTable(PokerTable pokerTable) throws PokerDataBaseException {
		int iRet = -1;
		try {
			Connection con = DBManager.getInstance().getConnection();
			PreparedStatement pstmt = con.prepareStatement(DELETE);
			pstmt.setInt(1, pokerTable.getId());
			iRet = pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException e) {
			throw interceptor.interceptException(e);
		}

		//return iRet;

	}

	public synchronized int modify(PokerTable t) throws PokerDataBaseException {
		int iRet = -1;
		try {
			Connection con = DBManager.getInstance().getConnection();
			PreparedStatement pstmt = con.prepareStatement(UPDATE);
			for (int i = 0; i < columns.length; i++) {
				Object valami = t.get(i);
				pstmt.setObject(i+1, valami);
			}
			pstmt.setObject(columns.length + 1, t.getId());
			iRet = pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException e) {
			throw interceptor.interceptException(e);
		}
		return iRet;

	}
	
	private void loadColumns() throws PokerDataBaseException {
		Connection con = DBManager.getInstance().getConnection();
		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(FIND_ALL);
			int asd = rs.getMetaData().getColumnCount() - 1;
			columns = new String[asd];
			for (int i = 0; i < asd; i++) {
				columns[i] = rs.getMetaData().getColumnLabel(i + 2);
			}
		} catch (SQLException e) {
			throw interceptor.interceptException(e);
		}
		createQueries();
	}

	private void createQueries() throws PokerDataBaseException {
		INSERT = "INSERT INTO " + TABLE_NAME + columnsToString() + "VALUES "+ qrySuffix();
		UPDATE = createQueryForUpdate();
	}

	private String createQueryForUpdate() {
		StringBuilder sb = new StringBuilder("UPDATE " + TABLE_NAME + " SET " + columns[0] + "=?");
		for (int i = 1; i < columns.length; i++) {
			sb.append("," + columns[i] + "=?");
		}
		sb.append(" WHERE id=?;");
		return sb.toString();
	}

	private String columnsToString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(" + columns[0]);
		for (int i = 1; i < columns.length; i++) {
			sb.append("," + columns[i]);
		}
		sb.append(") ");
		return sb.toString();
	}

	private String qrySuffix() {
		StringBuilder sb = new StringBuilder();
		sb.append(" (?");
		for (int i = 1; i < columns.length; i++) {
			sb.append(",?");
		}
		sb.append(");");
		return sb.toString();
	}
}	
