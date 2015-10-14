package com.spinalcraft.berberos.authserver;

import java.net.Socket;

public class ClientHandler implements Runnable{
	private Socket conn;
	
	public ClientHandler(Socket conn){
		this.conn = conn;
	}
	
	@Override
	public void run(){
		Receiver receiver = new Receiver(conn, Crypt.getInstance());
		
		if(receiver.receiveMessage())
			processRequest(receiver);
	}
	
	private void processRequest(Receiver receiver){
		String intent = receiver.getHeader("intent");
		switch(intent){
		case "ticket":
			(new TicketRequest(receiver, conn)).process();
			break;
		case "registerService":
			(new RegistrationRequest(receiver, conn)).process();
			break;
		case "testCredentials":
			(new CredentialVerification(receiver, conn)).process();
			break;
		}
	}
}



