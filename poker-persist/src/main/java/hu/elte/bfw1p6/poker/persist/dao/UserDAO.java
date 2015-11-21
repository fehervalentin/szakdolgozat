package hu.elte.bfw1p6.poker.persist.dao;

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
		try(Statement stmt = dbManager.getConnection().createStatement()) {
			List<User> users = new ArrayList<>();
			ResultSet rs = stmt.executeQuery(FIND_ALL);
			while (rs.next()) {
				User u = new User();
				for (int i = 0; i < columns.length; i++) {
					u.set(i, rs.getObject(columns[i]));
				}
				u.setId(rs.getInt(ID_COLUMN));
				users.add(u);
			}
			return users;
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
	}
	
	/**
	 * Felhaználónév alapján keres entitást az adatbázisban.
	 * @param username a felhasználónév
	 * @return ha létezik ilyen entitás, akkor a megtalált entitás, különben null
	 * @throws PokerDataBaseException
	 */
	public User findByUserName(String username) throws PokerDataBaseException {
		String query = "SELECT * FROM " + User.TABLE_NAME + " WHERE username=?;";
		try(PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query)) {
			User u = null;
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				u = new User();
				for (int i = 0; i < columns.length; i++) {
					u.set(i, rs.getObject(columns[i]));
				}
				u.setId(rs.getInt(ID_COLUMN));
			}
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
		String query = "UPDATE " + User.TABLE_NAME + " SET password=? WHERE username=?;";
		try(PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query)) {
			pstmt.setString(1, newPassword);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
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
		try(PreparedStatement pstmt = dbManager.getConnection().prepareStatement(DELETE)) {
			User u = findByUserName(player.getUserName());
			pstmt.setLong(1, u.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw sqlExceptionTranslator.interceptException(e);
		}
	}
}