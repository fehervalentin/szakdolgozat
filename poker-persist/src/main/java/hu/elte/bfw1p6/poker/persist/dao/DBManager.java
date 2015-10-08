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

	private static Connection getSQLServerConnection() {
		Connection con = null;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			String URL = "jdbc:sqlserver://localhost;databaseName=NID;user=sa;password=123;";
			con = DriverManager.getConnection(URL);
		} catch (Exception e) {
			System.out.println(e);
		}
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

	/**
	 * Connection to Microsoft Access
	 */
	private static Connection getMsAccessConnection() {
		Connection con = null;
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

			//String filename = "D:/Working/DB/";
			String filename = "Province.mdb";
			String URL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
			URL += filename + ";}";

			con = DriverManager.getConnection(URL);

		} catch (Exception se) {
			System.out.println(se);
		}

		return con;
	}
}