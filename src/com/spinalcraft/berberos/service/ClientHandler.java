package com.spinalcraft.berberos.service;

import java.net.Socket;

import javax.crypto.SecretKey;

import com.google.gson.JsonObject;
import com.spinalcraft.berberos.authserver.Crypt;
import com.spinalcraft.berberos.authserver.Sender;
import com.spinalcraft.berberos.common.Authenticator;
import com.spinalcraft.easycrypt.messenger.Message;

public class ClientHandler {
	private Socket socket;
	private SecretKey sessionKey;
	
	public ClientHandler(Socket socket, SecretKey sessionKey){
		this.socket = socket;
		this.sessionKey = sessionKey;
		sendAuthenticator();
	}
	
	private void sendAuthenticator(){
		Sender sender = new Sender(socket, Crypt.getInstance());
		sender.addHeader("status", "good");
		Authenticator authenticator = new Authenticator();
		authenticator.identity = "Service";
		authenticator.timestamp = System.currentTimeMillis() / 1000;
		JsonObject obj = authenticator.getJson();
		byte[] cipher = Crypt.getInstance().encryptMessage(sessionKey, obj.toString());
		sender.addItem("authenticator", Crypt.getInstance().encode(cipher));
		sender.sendMessage();
	}
	
	public Message getRequest(){
//		Receiver receiver = new Receiver(socket, Crypt.getInstance());
		
		
		
		return null;
	}
}
