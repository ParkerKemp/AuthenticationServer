package com.spinalcraft.berberos.authserver;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GetAuth {
	public static String get(){
		String random = Crypt.getInstance().randomString();
		String url = "http://auth.spinalcraft.com?accessKey=" + random;
		
		if(insert(random))
			return url;
		else
			return null;
	}
	
	private static boolean insert(String key){
		String query = "INSERT INTO accessKeys (accessKey) VALUES (?)";
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
