package com.spinalcraft.berberos.authserver;

import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import com.spinalcraft.berberos.client.ClientTicket;
import com.spinalcraft.berberos.service.ServiceTicket;

public class TicketRequest extends Responder{
	private static final long ticketDuration = 60 * 60 * 12; //12 hours (in seconds)
	
	public TicketRequest(Receiver receiver, Socket socket){
		super(receiver, socket);
	}
	
	public void process(){
		String identity = receiver.getHeader("identity");
		SecretKey clientKey = clientKey(identity);
		if(clientKey == null){
			sendDenial();
			return;
		}
		
		String service = receiver.getItem("service");
		SecretKey serviceKey = serviceKey(service);
		if(serviceKey == null){
			sendDenial();
			return;
		}
		
		SecretKey sessionKey;
		try {
			sessionKey = Crypt.getInstance().generateSecretKey();
			
			long expiration = getExpiration();
			
			ClientTicket clientTicket = generateClientTicket(identity, sessionKey, expiration);
			ServiceTicket serviceTicket = generateServiceTicket(identity, service, sessionKey, expiration);
			
			String clientTicketCipher = Crypt.getInstance().encode(Crypt.getInstance().encryptMessage(clientKey, clientTicket.getJson().toString()));
			String serviceTicketCipher = Crypt.getInstance().encode(Crypt.getInstance().encryptMessage(serviceKey, serviceTicket.getJson().toString()));
			
//			String clientTicketDecrypted = Crypt.getInstance().decryptMessage(clientKey, Crypt.getInstance().decode(clientTicketCipher));
			System.out.println("Client key: " + Crypt.getInstance().stringFromSecretKey(clientKey));
			
			Sender sender = new Sender(socket, Crypt.getInstance());
			sender.addHeader("status", "good");
			sender.addItem("serviceTicket", serviceTicketCipher);
			sender.addItem("clientTicket", clientTicketCipher);
			
			sender.sendMessage();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}
	
	private long getExpiration(){
		return (System.currentTimeMillis() / 1000) + ticketDuration;
	}
	
	private ClientTicket generateClientTicket(String identity, SecretKey sessionKey, long expiration){
		ClientTicket ticket = new ClientTicket(Crypt.getInstance());
		ticket.identity = identity;
		ticket.sessionKey = sessionKey;
		ticket.expiration = expiration;
		
		return ticket;
	}
	
	private ServiceTicket generateServiceTicket(String clientIdentity, String serviceIdentity, SecretKey sessionKey, long expiration){
		ServiceTicket ticket = new ServiceTicket(Crypt.getInstance());
		ticket.clientIdentity = clientIdentity;
		ticket.serviceIdentity = serviceIdentity;
		ticket.sessionKey = sessionKey;
		ticket.expiration = expiration;
		
		return ticket;
	}
}
