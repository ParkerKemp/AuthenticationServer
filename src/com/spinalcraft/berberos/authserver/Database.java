package com.spinalcraft.berberos.authserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
	private static Database instance;
	
	private Connection conn;
	private String dbName;
	
	private Database(){
		//Singleton
	}
	
	public static Database getInstance(){
		if(instance == null)
			instance = new Database();
		return instance;
	}
	
	public void init(String dbName){
		this.dbName = dbName;
		try {
			connect();
			createTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public PreparedStatement prepareStatement(String query) throws SQLException{
		return conn.prepareStatement(query);
	}
	
	private void connect() throws SQLException{
		conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "password");
		
		String query = "CREATE DATABASE IF NOT EXISTS " + dbName;
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.execute();
		
		query = "USE " + dbName;
		stmt = conn.prepareStatement(query);
		stmt.execute();
	}
	
	private void createTables() throws SQLException{
		String query = "CREATE TABLE IF NOT EXISTS users (identity VARCHAR(32) PRIMARY KEY, hash TINYTEXT)";
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.execute();
		
		query = "CREATE TABLE IF NOT EXISTS services (identity VARCHAR(32) PRIMARY KEY, secretKey TINYTEXT)";
		stmt = conn.prepareStatement(query);
		stmt.execute();
		
		
		query = "CREATE TABLE IF NOT EXISTS accessKeys ("
				+ "id INT PRIMARY KEY AUTO_INCREMENT, "
				+ "accessKey VARCHAR(32) NOT NULL UNIQUE, "
				+ "type INT NOT NULL, "
				+ "claimed TINYINT NOT NULL DEFAULT 0)";
		
		stmt = conn.prepareStatement(query);
		stmt.execute();
	}
}
