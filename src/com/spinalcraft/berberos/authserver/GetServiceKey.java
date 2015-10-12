package com.spinalcraft.berberos.authserver;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GetServiceKey {
	public static String get(){
		String random = Crypt.getInstance().randomString();
		
		if(insert(random))
			return random;
		else
			return null;
	}
	
	private static boolean insert(String key){
		String query = "INSERT INTO accessKeys (accessKey, type) VALUES (?, 2)";
		PreparedStatement stmt;
		try {
			stmt = Database.getInstance().prepareStatement(query);
			
			stmt.setString(1, key);
			
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
