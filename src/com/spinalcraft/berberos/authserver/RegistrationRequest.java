package com.spinalcraft.berberos.authserver;

import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.SecretKey;

public class RegistrationRequest {
	private Receiver receiver;
	private Socket socket;
	public RegistrationRequest(Receiver receiver, Socket socket){
		this.receiver = receiver;
		this.socket = socket;
	}
	
	public void process(){
		String identity = receiver.getItem("identity");
		String accessKey = receiver.getItem("accessKey");
		String publicKeyString = receiver.getItem("publicKey");
		if(!keyIsUnclaimed(accessKey))
			return;
		
		try {
			PublicKey publicKey = Crypt.getInstance().loadPublicKey(publicKeyString);
			SecretKey secretKey = Crypt.getInstance().generateSecretKey();
			insertService(identity, Crypt.getInstance().stringFromSecretKey(secretKey));
			claimKey(accessKey);
			sendResponse(publicKey, secretKey);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
	
	private void sendResponse(PublicKey publicKey, SecretKey secretKey){
		Sender sender = new Sender(socket, Crypt.getInstance());
		sender.addHeader("status", "good");
		sender.addItem("secretKey", Crypt.getInstance().encode(Crypt.getInstance().encryptKey(publicKey, secretKey)));
		sender.sendMessage();
	}
	
	private void insertService(String serviceName, String secretKey){
		String query = "INSERT INTO services (identity, secretKey) VALUES (?, ?)";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, serviceName);
			stmt.setString(2, secretKey);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void claimKey(String accessKey){
		String query = "UPDATE accessKeys SET claimed = 1 WHERE accessKey = ?";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, accessKey);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private boolean keyIsUnclaimed(String accessKey){
		String query = "SELECT * FROM accessKeys WHERE accessKey = ? AND type = 2 AND claimed = 0";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, accessKey);
			ResultSet rs = stmt.executeQuery();
			return rs.first();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
