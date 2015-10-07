package com.spinalcraft.authenticationserver;

import java.net.Socket;

import com.spinalcraft.easycrypt.messenger.MessageSender;
import com.spinalcraft.manager.server.communication.Crypt;

public class Sender extends MessageSender{

	public Sender(Socket socket, Crypt crypt) {
		super(socket, crypt);
	}
	
	@Override
	public void setIdentifier(String id){
		super.setIdentifier(id);
	}
}
