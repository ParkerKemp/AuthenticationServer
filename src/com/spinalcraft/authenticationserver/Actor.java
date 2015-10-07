package com.spinalcraft.authenticationserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.SecretKey;

public class Actor {
	public int id;
	public String name;
	public SecretKey secretKey;

	public static Actor getFromUnclaimedAccessKey(String key) throws SQLException{
		String query = "SELECT * FROM accessKeys k JOIN actors a ON k.actor_id = a.id "
				+ "WHERE BINARY k.accessKey = ? AND k.claimed = 0";
		
		PreparedStatement stmt = Database.getInstance().prepareStatement(query);
		
		stmt.setString(1, key);
		
		ResultSet rs = stmt.executeQuery();
		if(!rs.first()){
			return null;
		}
		
		Actor actor = new Actor();
		actor.id = rs.getInt("a.id");
		actor.name = rs.getString("a.name");
		
		return actor;
	}
	
	public String getSecretKeyAsString(){
		return Crypt.getInstance().stringFromSecretKey(secretKey);
	}
	
	public void updateWithApproval() throws SQLException{
		String query = "UPDATE manager_actors "
				+ "SET key = ?"
				+ "WHERE id = ?";
		PreparedStatement stmt = Database.getInstance().prepareStatement(query);
		stmt.setString(2, getSecretKeyAsString());
		stmt.setInt(3, id);
		stmt.execute();
		
		updateWithClaimedAccessKey();
	}
	
	private void updateWithClaimedAccessKey() throws SQLException{
		String query = "UPDATE accessKeys "
				+ "SET claimed = 1 "
				+ "WHERE actor_id = ? ";
		PreparedStatement stmt = Database.getInstance().prepareStatement(query);
		stmt.setInt(1, id);
		stmt.execute();
	}
}
