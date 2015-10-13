package hu.elte.bfw1p6.poker.persist.pokertable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.persist.dao.DBManager;

public class PokerTableRepository {
	private final String TABLE_NAME = "poker_tables";

	private static String[] columns;
	private static PokerTableRepository instance = null;

	private String FIND_ALL = "SELECT * FROM " + TABLE_NAME + ";";
	private String INSERT;
	private String UPDATE;
	private PokerTableRepository() {
		loadColumns();
	}

	public static synchronized PokerTableRepository getInstance() {
		if (instance == null) {
			instance = new PokerTableRepository();
		}
		return instance;
	}

	private void loadColumns() {
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
			e.printStackTrace();
		}
		createQueries();
	}

	private void createQueries() {
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

	public int save(PokerTable t) throws SQLException {
		int iRet = -1;
		Connection con = DBManager.getInstance().getConnection();
		PreparedStatement pstmt = con.prepareStatement(INSERT);
		for (int i = 0; i < columns.length; i++) {
			Object valami = t.get(i);
			pstmt.setObject(i+1, valami);
		}
		iRet = pstmt.executeUpdate();

		pstmt.close();

		return iRet;
	}

	public List<PokerTable> findAll() {
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
		} catch (SQLException se) {
			System.out.println(se);
		}
		return tables;
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

	public int modify(PokerTable t) throws SQLException {
		int iRet = -1;
		Connection con = DBManager.getInstance().getConnection();
		PreparedStatement pstmt = con.prepareStatement(UPDATE);
		for (int i = 0; i < columns.length; i++) {
			Object valami = t.get(i);
			pstmt.setObject(i+1, valami);
		}
		pstmt.setObject(columns.length + 1, t.getId());
		iRet = pstmt.executeUpdate();

		pstmt.close();

		return iRet;
		
	}

	/*public static User findUserByUserName(String username) {
		User u = null;
		try {
			String QRY = "SELECT * FROM User WHERE username=?";
			Connection con = DBManager.getInstance().getConnection();
			PreparedStatement pstmt = con.prepareStatement(QRY);
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				u = new User();
				u.setId(rs.getInt("id"));
				u.setPassword(rs.getString("password"));
				u.setAmount(rs.getBigDecimal("balance"));
				u.setRegDate(rs.getLong("reg_date"));
			}

			pstmt.close();
			return u;
		} catch (SQLException se) {
			System.out.println(se);
		}
		return u;
	}

	public static int modifyPassword(User u) {
		int iRet = -1;
		try {
			Connection con = DBManager.getInstance().getConnection();
			String SQL = "UPDATE User SET password=? WHERE Id=?";
			PreparedStatement pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, u.getPassword());
			pstmt.setLong(2, u.getId());

			iRet = pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException se) {
			System.out.println(se);
		}

		return iRet;
	}

	public static int delete(User u) {
		int iRet = -1;
		try {
			Connection con = DBManager.getInstance().getConnection();
			String SQL = "DELETE FROM Province WHERE Id=?";
			PreparedStatement pstmt = con.prepareStatement(SQL);
			pstmt.setLong(1, u.getId());

			iRet = pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException se) {
			System.out.println(se);
		}
		return iRet;
	}

	public static void deleteAll() {
		Connection con = DBManager.getInstance().getConnection();
		try {
			con.setAutoCommit(false);
			String SQL = "DELETE FROM User";
			PreparedStatement pstmt = con.prepareStatement(SQL);

			pstmt.executeUpdate();
			con.commit();
		} catch (SQLException se) {
			try {
				con.rollback();
			} catch (SQLException ise) {
			}
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException fse) {
			}
		}
	}

	public static ArrayList findAll() {
		ArrayList arr = new ArrayList();

		try {
			String QRY = "SELECT * FROM Province ORDER BY Id";
			Connection con = DBManager.getInstance().getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(QRY);

			while (rs.next()) {
				Province p = new Province();
				p.setId(rs.getInt("Id"));
				p.setShortName(rs.getString("ShortName"));
				p.setName(rs.getString("Name"));
				arr.add(p);
			}

			stmt.close();
		} catch (SQLException se) {
			System.out.println(se);
		}
		return arr;
	}*/

	/*public static ArrayList findByName(String name) {
		ArrayList arr = new ArrayList();

		try {
			String QRY = "SELECT * FROM Province WHERE name LIKE(?) ORDER BY id";
			Connection con = DBManager.getInstance().getConnection();
			PreparedStatement pstmt = con.prepareStatement(QRY);
			pstmt.setString(1, "%" + name + "%");
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				User p = new User();
				p.setId(rs.getInt("Id"));
				p.setShortName(rs.getString("ShortName"));
				p.setName(rs.getString("Name"));
				arr.add(p);
			}

			pstmt.close();
		} catch (SQLException se) {
			System.out.println(se);
		}
		return arr;
	}*/
}	
