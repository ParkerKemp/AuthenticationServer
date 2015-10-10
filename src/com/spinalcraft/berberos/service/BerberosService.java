package com.spinalcraft.berberos.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyPair;

import com.spinalcraft.berberos.authserver.Crypt;
import com.spinalcraft.berberos.authserver.Receiver;
import com.spinalcraft.berberos.authserver.Sender;

public abstract class BerberosService {
	public boolean register(String accessKey){
		if(retrieveSecretKey() != null){
			return true;
		}
		Socket socket = new Socket();
		try {
			KeyPair keyPair = Crypt.getInstance().generateKeys();
			socket.connect(new InetSocketAddress("auth.spinalcraft.com", 9494), 5000);
			Sender sender = new Sender(socket, Crypt.getInstance());
			sender.addHeader("intent", "serviceAuth");
			sender.addItem("publicKey", Crypt.getInstance().stringFromPublicKey(keyPair.getPublic()));
			sender.addItem("accessKey", accessKey);
			sender.sendMessage();
			
			Receiver receiver = new Receiver(socket, Crypt.getInstance());
			receiver.receiveMessage();
			if(receiver.getHeader("status").equals("good")){
				String secretKey = receiver.getItem("secretKey");
				storeSecretKey(secretKey);
				return true;
			}
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected abstract void storeSecretKey(String secretKey);
	
	protected abstract String retrieveSecretKey();
}
