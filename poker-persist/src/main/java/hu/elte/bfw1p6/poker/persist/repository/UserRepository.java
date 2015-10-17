package hu.elte.bfw1p6.poker.persist.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.model.entity.Player;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.dao.DBManager;
import hu.elte.bfw1p6.poker.persist.dao.SQLExceptionInterceptor;

public class UserRepository {
	private static final String TABLE_NAME = "users";

	private static String[] columns;

	private String FIND_ALL = "SELECT * FROM " + TABLE_NAME + ";";
	private String INSERT;
	private String UPDATE;
	private String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id=?;";

	private static UserRepository instance = null;

	private SQLExceptionInterceptor interceptor = SQLExceptionInterceptor.getInstance();

	private UserRepository() throws PokerDataBaseException {
		loadColumns();
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
			String SQL = "INSERT INTO users(username, balacen, reg_date, password) Values(?,?,?,?)";
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
			throw interceptor.interceptException(e);
		}
	}

	public User findUserByUserName(String username) throws PokerDataBaseException {
		try {
			User u = null;
			String QRY = "SELECT * FROM " + TABLE_NAME + " WHERE username=?";
			Connection con = DBManager.getInstance().getConnection();
			PreparedStatement pstmt = con.prepareStatement(QRY);
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				u = new User();
				u.setId(rs.getInt("id"));
				u.setPassword(rs.getString("password"));
				u.setAmount(rs.getBigDecimal("balance"));
			}

			pstmt.close();
			return u;
		} catch (SQLException e) {
			throw interceptor.interceptException(e);
		}
	}

	public void modifyPassword(String username, String newPassword) throws PokerDataBaseException {
		try {
			Connection con = DBManager.getInstance().getConnection();
			String SQL = "UPDATE User SET password=? WHERE username=?";
			PreparedStatement pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, newPassword);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw interceptor.interceptException(e);
		}
	}

	public void deletePlayer(Player player) throws PokerDataBaseException {
		try {
			User u = findUserByUserName(player.getUserName());
			Connection con = DBManager.getInstance().getConnection();
			String SQL = DELETE;
			PreparedStatement pstmt = con.prepareStatement(SQL);
			pstmt.setLong(1, u.getId());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw interceptor.interceptException(e);
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

	public Player getPlayerByName(String username) throws PokerDataBaseException {
		User user = findUserByUserName(username);
		Player player = new Player(user.getUserName(), user.getBalance(), user.getRegDate());
		return player;
	}
}	
