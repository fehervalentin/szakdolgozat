package hu.elte.bfw1p6.poker.persist.dao;

import java.sql.*;

public final class DBManager {

	private static DBManager instance = null;
	private Connection con = null;

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

			String strCon = "jdbc:mysql://127.0.0.1/pokerdb?user=root&password=1234";
			con = DriverManager.getConnection(strCon);
		} catch (SQLException se) {
			System.out.println(se);
		}
		return con;
	}
}