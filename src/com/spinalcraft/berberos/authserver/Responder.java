package com.spinalcraft.berberos.authserver;

import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.SecretKey;

public abstract class Responder {
	protected Receiver receiver;
	protected Socket socket;
	
	public Responder(Receiver receiver, Socket socket){
		this.receiver = receiver;
		this.socket = socket;
	}
	
	protected abstract void process();
	
	protected SecretKey serviceKey(String service){
		String query = "SELECT secretKey FROM services WHERE identity = ?";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, service);
			ResultSet rs = stmt.executeQuery();
			if(!rs.first())
				return null;
			String keyString = rs.getString("secretKey");
			return Crypt.getInstance().loadSecretKey(keyString);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected SecretKey clientKey(String identity){
		String query = "SELECT hash FROM users WHERE identity = ?";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, identity);
			ResultSet rs = stmt.executeQuery();
			if(!rs.first())
				return null;
			String hash = rs.getString("hash");
			return Crypt.getInstance().loadSecretKey(hash);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected void sendDenial(){
		Sender sender = new Sender(socket, Crypt.getInstance());
		sender.addHeader("status", "bad");
		sender.sendMessage();
	}
}
