package com.spinalcraft.berberos.authserver;

import java.net.Socket;

//import javax.crypto.SecretKey;

import com.spinalcraft.easycrypt.messenger.MessageReceiver;

public class Receiver extends MessageReceiver{

	public Receiver(Socket socket, Crypt crypt) {
		super(socket, crypt);
	}
}
