package com.spinalcraft.berberos.client;

import java.net.Socket;

import javax.crypto.SecretKey;

import com.spinalcraft.berberos.common.Ambassador;

public class ClientAmbassador extends Ambassador{

	public ClientAmbassador(Socket socket, SecretKey sessionKey) {
		super(socket, sessionKey);
	}
}
