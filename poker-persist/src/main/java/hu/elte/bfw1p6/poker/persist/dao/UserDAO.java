package hu.elte.bfw1p6.poker.persist.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;
import hu.elte.bfw1p6.poker.model.entity.User;

/**
 * A felhasználók letárolásáért felelős osztály.
 * @author feher
 *
 */
public class UserDAO extends GenericDAO<User>{
	
	public UserDAO() throws PokerDataBaseException {
		super(User.TABLE_NAME);
	}

	@Override
	public List<User> findAll() throws PokerDataBaseException {
		List<User> users = new ArrayList<>();

		try {
			String QRY = FIND_ALL;
			Connection con = dbManager.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(QRY);

			while (rs.next()) {
				User u = new User();
				for (int i = 0; i < columns.length; i++) {
					u.set(i, rs.getObject(columns[i]));
				}
				u.setId(rs.getInt(ID_COLUMN));
				users.add(u);
			}

			stmt.close();
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
		return users;
	}
	
	/**
	 * Felhaználónév alapján keres entitást az adatbázisban.
	 * @param username a felhasználónév
	 * @return ha létezik ilyen entitás, akkor a megtalált entitás, különben null
	 * @throws PokerDataBaseException
	 */
	public User findByUserName(String username) throws PokerDataBaseException {
		try {
			User u = null;
			String QRY = "SELECT * FROM " + User.TABLE_NAME + " WHERE username=?";
			Connection con = dbManager.getConnection();
			PreparedStatement pstmt = con.prepareStatement(QRY);
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				u = new User();
				for (int i = 0; i < columns.length; i++) {
					u.set(i, rs.getObject(columns[i]));
				}
				u.setId(rs.getInt(ID_COLUMN));
			}

			pstmt.close();
			return u;
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
	}
	
	/**
	 * Adott felhasználónévhez új jelszót rendel.
	 * @param username a felhasználónév
	 * @param newPassword az új jelszó
	 * @throws PokerDataBaseException
	 */
	public void modifyPassword(String username, String newPassword) throws PokerDataBaseException {
		try {
			Connection con = dbManager.getConnection();
			String SQL = "UPDATE " + User.TABLE_NAME + " SET password=? WHERE username=?";
			PreparedStatement pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, newPassword);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
	}
	
	/**
	 * Létező user entitást töröl az adatbázisból.
	 * @param player az entitás
	 * @throws PokerDataBaseException
	 */
	public void deletePlayer(PokerPlayer player) throws PokerDataBaseException {
		try {
			User u = findByUserName(player.getUserName());
			Connection con = dbManager.getConnection();
			String SQL = DELETE;
			PreparedStatement pstmt = con.prepareStatement(SQL);
			pstmt.setLong(1, u.getId());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
	}
}