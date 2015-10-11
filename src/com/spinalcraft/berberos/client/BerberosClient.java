package com.spinalcraft.berberos.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import com.spinalcraft.berberos.authserver.Crypt;
import com.spinalcraft.berberos.authserver.Receiver;
import com.spinalcraft.berberos.authserver.Sender;
import com.spinalcraft.berberos.common.Ambassador;
import com.spinalcraft.berberos.common.Authenticator;
import com.spinalcraft.berberos.common.ClientTicket;

public abstract class BerberosClient {
	
	public BerberosClient(){
	}
	
	public Ambassador getAmbassador(String username, String password, String service){
		SecretKey secretKey = Crypt.getInstance().loadSecretKey(getHash(password));
		String serviceTicket = retrieveTicket(service);
		String sessionKeyString = retrieveSessionKey(service);
		SecretKey sessionKey;
		if(serviceTicket == null || sessionKeyString == null){
			Receiver receiver = requestTicket(username, secretKey, service);
			serviceTicket = extractServiceTicket(receiver, service);
			ClientTicket clientTicket = extractClientTicket(receiver, secretKey);
			sessionKey = clientTicket.sessionKey;
		}
		else{
			sessionKey = Crypt.getInstance().loadSecretKey(sessionKeyString);
		}
		
		if(serviceTicket == null)
			return null;
		
		Socket socket = new Socket();
		Ambassador ambassador = null;
		try {
			socket.connect(new InetSocketAddress("mc.spinalcraft.com", 9494), 5000);
			
			sendHandshakeRequest(socket, username, sessionKey, serviceTicket);
			if(!receiveHandshakeResponse(socket, sessionKey, service))
				return null;
			
			ambassador = new Ambassador(socket, sessionKey);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ambassador;
	}
	
	private boolean receiveHandshakeResponse(Socket socket, SecretKey sessionKey, String service){
		Receiver receiver = new Receiver(socket, Crypt.getInstance());
		receiver.receiveMessage();
		String serviceIdentity = receiver.getItem("identity");
		
		return serviceIdentity.equals(service);
	}
	
	private void sendHandshakeRequest(Socket socket, String identity, SecretKey sessionKey, String serviceTicket){
		Sender sender = new Sender(socket, Crypt.getInstance());
		sender.addItem("ticket", serviceTicket);
		sender.addItem("authenticator", getAuthenticator(identity, sessionKey));
		sender.sendMessage();
	}
	
	private String getAuthenticator(String identity, SecretKey sessionKey){
		Authenticator authenticator = new Authenticator();
		authenticator.identity = identity;
		authenticator.timestamp = System.currentTimeMillis() / 1000;
		String json = authenticator.getJson().getAsString();
		byte[] cipher = Crypt.getInstance().encryptMessage(sessionKey, json);
		return Crypt.getInstance().encode(cipher);
	}
	
	private Receiver requestTicket(String identity, SecretKey secretKey, String service){
		Socket socket = new Socket(); 
		try {
			socket.connect(new InetSocketAddress("auth.spinalcraft.com", 9494), 5000);
			Sender sender = new Sender(socket, Crypt.getInstance());
			sender.addHeader("identity", identity);
			sender.addHeader("intent", "ticket");
			sender.addItem("service", service);
			sender.sendMessage();
			
			Receiver receiver = new Receiver(socket, Crypt.getInstance());
			receiver.receiveMessage();
			socket.close();
			return receiver;
			
//			String clientTicketCipher = receiver.getItem("clientTicket");
//			ClientTicket clientTicket = ClientTicket.fromCipher(clientTicketCipher, secretKey);
//			if(clientTicket == null)
//				return null;
//			
//			String serviceTicketCipher = receiver.getItem("serviceTicket");
//			cacheTicket(service, serviceTicketCipher);
//
//			return serviceTicketCipher;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String extractServiceTicket(Receiver receiver, String service){
		String serviceTicketCipher = receiver.getItem("serviceTicket");
		cacheTicket(service, serviceTicketCipher);

		return serviceTicketCipher;
	}
	
	private ClientTicket extractClientTicket(Receiver receiver, SecretKey secretKey){
		String clientTicketCipher = receiver.getItem("clientTicket");
		return ClientTicket.fromCipher(clientTicketCipher, secretKey);
	}
	
	private String getHash(String password){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");

			md.update(password.getBytes("UTF-8"));
			byte[] digest = md.digest();
			return new String(digest);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected abstract void cacheSessionKey(String service, String sessionKey);
	
	protected abstract String retrieveSessionKey(String service);
	
	protected abstract void cacheTicket(String service, String ticket);
	
	protected abstract String retrieveTicket(String service);
}
