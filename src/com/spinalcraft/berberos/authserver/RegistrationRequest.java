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
		String serviceAddress = receiver.getItem("serviceAddress");
		int servicePort = Integer.parseInt(receiver.getItem("servicePort"));
		String publicKeyString = receiver.getItem("publicKey");
		if(!keyIsUnclaimed(accessKey)){
			sendDenial();
			return;
		}
		
		try {
			PublicKey publicKey = Crypt.getInstance().loadPublicKey(publicKeyString);
			SecretKey secretKey = Crypt.getInstance().generateSecretKey();
			if(insertService(identity, Crypt.getInstance().stringFromSecretKey(secretKey), serviceAddress, servicePort) && claimKey(accessKey))
				sendResponse(publicKey, secretKey);
			else
				sendDenial();
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
	
	private void sendDenial(){
		Sender sender = new Sender(socket, Crypt.getInstance());
		sender.addHeader("status", "bad");
		sender.sendMessage();
	}
	
	private boolean insertService(String serviceName, String secretKey, String serviceAddress, int servicePort){
		String query = "INSERT INTO services (identity, secretKey, serviceAddress, servicePort) VALUES (?, ?, ?, ?)";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, serviceName);
			stmt.setString(2, secretKey);
			stmt.setString(3, serviceAddress);
			stmt.setInt(4, servicePort);
			stmt.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean claimKey(String accessKey){
		String query = "UPDATE accessKeys SET claimed = 1 WHERE accessKey = ?";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, accessKey);
			stmt.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
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
