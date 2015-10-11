package com.spinalcraft.berberos.common;

import java.io.IOException;
import java.net.Socket;

import javax.crypto.SecretKey;

import com.spinalcraft.berberos.authserver.Crypt;
import com.spinalcraft.berberos.authserver.Receiver;
import com.spinalcraft.berberos.authserver.Sender;

public class Ambassador {
	protected Socket socket;
	protected SecretKey sessionKey;
	
	public Ambassador(Socket socket, SecretKey sessionKey){
		this.socket = socket;
		this.sessionKey = sessionKey;
	}
	
	public Sender getSender(){
		return new Sender(socket, Crypt.getInstance());
	}
	
	public boolean sendMessage(Sender sender) throws IOException{
		return sender.sendEncrypted(sessionKey);
	}
	
	public Receiver receiveMessage(){
		Receiver receiver = new Receiver(socket, Crypt.getInstance());
		receiver.receiveMessage(sessionKey);
		return receiver;
	}
}
