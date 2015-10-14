package com.spinalcraft.berberos.authserver;

import java.net.Socket;

import javax.crypto.SecretKey;

import com.spinalcraft.berberos.common.Authenticator;

public class CredentialVerification extends Responder{
	public CredentialVerification(Receiver receiver, Socket socket){
		super(receiver, socket);
	}
	
	public void process(){
		String identity = receiver.getHeader("identity");
		
		Authenticator authenticator = new Authenticator();
		authenticator.identity = "Berberos";
		authenticator.timestamp = System.currentTimeMillis() / 1000;
		
		SecretKey clientKey = clientKey(identity);
		if(clientKey == null){
			sendDenial();
			return;
		}
		
		String authCipher = Crypt.getInstance().encode(Crypt.getInstance().encryptMessage(clientKey, authenticator.getJson().toString()));
		Sender sender = new Sender(socket, Crypt.getInstance());
		sender.addHeader("status", "good");
		sender.addItem("authenticator", authCipher);
		sender.sendMessage();
	}
}
