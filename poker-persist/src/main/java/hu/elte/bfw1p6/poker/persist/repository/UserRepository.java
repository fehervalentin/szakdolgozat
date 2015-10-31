package hu.elte.bfw1p6.poker.persist.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.dao.DBManager;
import hu.elte.bfw1p6.poker.persist.dao.SQLExceptionTranslator;

public class UserRepository {
	private static final String TABLE_NAME = "users";

	private static String[] columns;

	private String FIND_ALL = "SELECT * FROM " + TABLE_NAME + ";";
	private String INSERT;
	private String UPDATE;
	private String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id=?;";

	private static UserRepository instance = null;

	private SQLExceptionTranslator sqlExceptionTranslator = SQLExceptionTranslator.getInstance();

	private UserRepository() throws PokerDataBaseException {
		loadColumns();
		UPDATE = createQueryForUpdate();
	}

	public static synchronized UserRepository getInstance() throws PokerDataBaseException {
		if (instance == null) {
			instance = new UserRepository();
		}
		return instance;
	}

	public void save(User u) throws PokerDataBaseException {
		try {
			Connection con = DBManager.getInstance().getConnection();
			String SQL = "INSERT INTO users(username, balance, reg_date, password, admin) Values(?,?,?,?,?)";
			PreparedStatement pstmt = con.prepareStatement(SQL);
			for (int i = 0; i < columns.length; i++) {
				pstmt.setObject(i+1, u.get(i));
			}
			/*pstmt.setString(1, u.getUserName());
			pstmt.setString(2, u.getPassword());
			pstmt.setBigDecimal(3, u.getBalance());
			pstmt.setLong(4, u.getRegDate());*/
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
	}

	public User findByUserName(String username) throws PokerDataBaseException {
		try {
			User u = null;
			String QRY = "SELECT * FROM " + TABLE_NAME + " WHERE username=?";
			Connection con = DBManager.getInstance().getConnection();
			PreparedStatement pstmt = con.prepareStatement(QRY);
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				u = new User();
				for (int i = 0; i < columns.length; i++) {
					u.set(i, rs.getObject(columns[i]));
				}
				u.setId(rs.getInt("id"));
			}

			pstmt.close();
			return u;
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
	}

	public void modifyPassword(String username, String newPassword) throws PokerDataBaseException {
		try {
			Connection con = DBManager.getInstance().getConnection();
			String SQL = "UPDATE " + TABLE_NAME + " SET password=? WHERE username=?";
			PreparedStatement pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, newPassword);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
	}

	public void deletePlayer(PokerPlayer player) throws PokerDataBaseException {
		try {
			User u = findByUserName(player.getUserName());
			Connection con = DBManager.getInstance().getConnection();
			String SQL = DELETE;
			PreparedStatement pstmt = con.prepareStatement(SQL);
			pstmt.setLong(1, u.getId());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
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
			throw sqlExceptionTranslator.interceptException(e);
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

	public PokerPlayer getPlayerByName(String username) throws PokerDataBaseException {
		User user = findByUserName(username);
		PokerPlayer player = new PokerPlayer(user.getUserName(), user.getBalance(), user.getRegDate());
		return player;
	}

	public synchronized void modify(User u) throws PokerDataBaseException {
		try {
//			System.out.println(u.getBalance());
			Connection con = DBManager.getInstance().getConnection();
			PreparedStatement pstmt = con.prepareStatement(UPDATE);
			for (int i = 0; i < columns.length; i++) {
				Object valami = u.get(i);
				pstmt.setObject(i+1, valami);
			}
			pstmt.setObject(columns.length + 1, u.getId());
			pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}

	}
}	
