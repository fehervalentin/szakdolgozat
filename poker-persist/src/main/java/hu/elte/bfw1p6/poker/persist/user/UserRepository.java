package hu.elte.bfw1p6.poker.persist.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.dao.DBManager;

public class UserRepository {
	private final String TABLE_NAME = "users";

	public static int save(User u) {
		int iRet = -1;
		try {
			Connection con = DBManager.getInstance().getConnection();
			String SQL = "INSERT INTO users(username, password, balance, reg_date) Values(?,?,?,?)";
			PreparedStatement pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, u.getUserName());
			pstmt.setString(2, u.getPassword());
			pstmt.setBigDecimal(3, u.getBalance());
			pstmt.setLong(4, u.getRegDate());

			iRet = pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException se) {
			System.out.println(se);
		}

		return iRet;
	}
	
	public static User findUserByUserName(String username) {
		User u = null;
		try {
			String QRY = "SELECT * FROM users WHERE username=?";
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

	/*public static ArrayList findAll() {
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
