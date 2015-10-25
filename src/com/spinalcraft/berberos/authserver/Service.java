package com.spinalcraft.berberos.authserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.SecretKey;

public class Service {
	public String identity;
	public SecretKey secretKey;
	public String serviceAddress;
	public int servicePort;
	
	public static Service fromIdentity(String identity){
		String query = "SELECT * FROM services WHERE identity = ?";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, identity);
			ResultSet rs = stmt.executeQuery();
			if(!rs.first())
				return null;
			Service service = new Service();
			service.identity = identity;
			service.secretKey = Crypt.getInstance().loadSecretKey(rs.getString("secretKey"));
			service.serviceAddress = rs.getString("serviceAddress");
			service.servicePort = rs.getInt("servicePort");
			return service;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
