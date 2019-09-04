package org.guildcraft.guildquests.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;

public class SQLHelper {
	private String ip;
	private String port;
	private String database;
	private String username;
	private String password;
	private Connection conn;

	public SQLHelper(String ip, String port, String database, String username, String password) {
		this.ip = ip;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	public synchronized boolean connect() {
		try {
			disconnect();
			conn = DriverManager.getConnection(
					"jdbc:mysql://" + ip + ":" + port + '/' + database + "?allowMultiQueries=true", username, password);
			return true;
		} catch (SQLException e) {
			conn = null;
			e.printStackTrace();
			return false;
		}
	}

	public synchronized void disconnect() {
		if (conn == null) {
			System.out.println("There is no connection to close!");
			return;
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void refreshConnection() {
		try {
			PreparedStatement s = conn.prepareStatement("SELECT 1");
			s.executeQuery();
		}
		catch (SQLException e) {
			connect();
		}
	}

	public synchronized boolean isConnected() {
		try {
			return (conn != null) && !conn.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

	public synchronized Connection getConnection() {
		return conn;
	}

	public synchronized void runUpdate(String query) {
		if (!isConnected()) {
			System.out.println("You need to open the connection to the database BEFORE you execute a query.");
			return;
		}

		try {
			Statement s = conn.createStatement();
			s.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public synchronized ResultSet runQuery(String query) {
		if (!isConnected()) {
			Bukkit.getConsoleSender()
					.sendMessage("You need to open the connection to the database BEFORE you execute a query.");
			return null;
		}

		try {
			Statement s = conn.createStatement();
			return s.executeQuery(query);
		} catch (Exception e) {
			return null;
		}
	}
}
