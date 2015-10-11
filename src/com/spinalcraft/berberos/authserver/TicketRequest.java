package com.spinalcraft.berberos.authserver;

import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.SecretKey;

import com.spinalcraft.berberos.common.ClientTicket;
import com.spinalcraft.berberos.common.ServiceTicket;

public class TicketRequest {
	private Receiver receiver;
	private Socket socket;
	
	public TicketRequest(Receiver receiver, Socket socket){
		this.receiver = receiver;
		this.socket = socket;
	}
	
	public void process(){
		String identity = receiver.getHeader("identity");
		SecretKey clientKey = clientKey(identity);
		if(clientKey == null)
			return;
		
		String service = receiver.getItem("service");
		SecretKey serviceKey = serviceKey(service);
		if(serviceKey == null)
			return;
		
		SecretKey sessionKey;
		try {
			sessionKey = Crypt.getInstance().generateSecretKey();
			
			ClientTicket clientTicket = generateClientTicket(identity, sessionKey);
			ServiceTicket serviceTicket = generateServiceTicket(identity, service, sessionKey);
			
			String clientTicketCipher = Crypt.getInstance().encode(Crypt.getInstance().encryptMessage(clientKey, clientTicket.getJson().toString()));
			String serviceTicketCipher = Crypt.getInstance().encode(Crypt.getInstance().encryptMessage(clientKey, serviceTicket.getJson().toString()));
			
			Sender sender = new Sender(socket, Crypt.getInstance());
			sender.addHeader("status", "good");
			sender.addItem("serviceTicket", clientTicketCipher);
			sender.addItem("clientTicket", serviceTicketCipher);
			
			sender.sendMessage();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}
	
	private ClientTicket generateClientTicket(String identity, SecretKey sessionKey){
		ClientTicket ticket = new ClientTicket();
		ticket.identity = identity;
		ticket.sessionKey = sessionKey;
		ticket.expiration = (System.currentTimeMillis() / 1000) + 60 * 60;
		
		return ticket;
	}
	
	private ServiceTicket generateServiceTicket(String clientIdentity, String serviceIdentity, SecretKey sessionKey){
		ServiceTicket ticket = new ServiceTicket();
		ticket.clientIdentity = clientIdentity;
		ticket.serviceIdentity = serviceIdentity;
		ticket.sessionKey = sessionKey;
		ticket.expiration = (System.currentTimeMillis() / 1000) + 60 * 60;
		
		return ticket;
	}
	
	private SecretKey serviceKey(String service){
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
	
	private SecretKey clientKey(String identity){
		String query = "SELECT secretKey FROM actors WHERE username = ?";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, identity);
			ResultSet rs = stmt.executeQuery();
			if(!rs.first())
				return null;
			String hash = rs.getString("secretKey");
			return Crypt.getInstance().loadSecretKey(hash);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
