package com.spinalcraft.authenticationserver;

import java.net.Socket;

import com.spinalcraft.easycrypt.messenger.MessageSender;

public class Sender extends MessageSender{

	public Sender(Socket socket, Crypt crypt) {
		super(socket, crypt);
	}
	
	@Override
	public void setIdentifier(String id){
		super.setIdentifier(id);
	}
}
