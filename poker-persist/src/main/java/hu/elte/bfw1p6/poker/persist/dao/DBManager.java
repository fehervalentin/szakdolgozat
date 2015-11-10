package hu.elte.bfw1p6.poker.persist.dao;

import java.sql.*;

/**
 * Az adatbázishoz való csatlakozást biztosító osztály.
 * @author feher
 *
 */
public final class DBManager {

	private static DBManager instance = null;
	private Connection con = null;
	
	private static final String IP = "127.0.0.1";
	private static final String DATABASE_NAME = "pokerdb";
	private static final String USERNAME="root";
	private static final String PASSWORD="1234";
	
	private static final String strCon = "jdbc:mysql://" + IP + "/" + DATABASE_NAME + "?user=" + USERNAME + "&password=" + PASSWORD;

	public DBManager() {
		con = getMySQLConnection();
	}

	public static synchronized DBManager getInstance() {
		if (instance == null) {
			instance = new DBManager();
		}
		return instance;
	}

	public Connection getConnection() {
		return con;
	}

	/**
	 * Connection to MySQL Database
	 */
	private static Connection getMySQLConnection() {
		Connection con = null;

		try {
			con = DriverManager.getConnection(strCon);
		} catch (SQLException e) {
			System.err.println(e);
		}
		return con;
	}
}