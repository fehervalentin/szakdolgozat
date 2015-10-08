package hu.elte.bfw1p6.poker.persist.ptable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import hu.elte.bfw1p6.poker.model.entity.PTable;
import hu.elte.bfw1p6.poker.model.entity.PokerType;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.dao.DBManager;

public class PTableRepository {

	public static int save(PTable t) {
		int iRet = -1;
		try {
			Connection con = DBManager.getInstance().getConnection();
			String SQL = "INSERT INTO PTable(name, max_time, max_players, max_bet, small_blind, big_blind, poker_type) Values(?,?,?,?,?,?,?)";
			PreparedStatement pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, t.getName());
			pstmt.setInt(2, t.getMaxTime());
			pstmt.setInt(3, t.getMaxPlayers());
			pstmt.setBigDecimal(4, t.getMaxBet());
			pstmt.setBigDecimal(5, t.getSmallBlind());
			pstmt.setBigDecimal(6, t.getBigBlind());
			pstmt.setString(7, t.getPokerType().getName());

			iRet = pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException se) {
			System.out.println(se);
		}

		return iRet;
	}
	
	public static List<PTable> findAll() {
		List<PTable> tables = new ArrayList<>();

		try {
			String QRY = "SELECT * FROM PTable";
			Connection con = DBManager.getInstance().getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(QRY);

			while (rs.next()) {
				PTable t = new PTable();
				t.setId(rs.getInt("id"));
				t.setName(rs.getString("name"));
				t.setPokerType(PokerType.valueOf(rs.getString("poker_type")));
				t.setMaxTime(rs.getInt("max_time"));
				t.setMaxPlayers(rs.getInt("max_players"));
				t.setSmallBlind(rs.getBigDecimal("small_blind"));
				t.setBigBlind(rs.getBigDecimal("big_blind"));
				t.setMaxBet(rs.getBigDecimal("max_bet"));
				tables.add(t);
			}

			stmt.close();
		} catch (SQLException se) {
			System.out.println(se);
		}
		return tables;
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
