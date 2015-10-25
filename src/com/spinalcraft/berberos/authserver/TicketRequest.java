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
		
		String serviceString = receiver.getItem("service");
		Service service = Service.fromIdentity(serviceString);
//		SecretKey serviceKey = serviceKey(service);
		if(service == null){
			sendDenial();
			return;
		}
		
		SecretKey sessionKey;
		try {
			sessionKey = Crypt.getInstance().generateSecretKey();
			
			long expiration = getExpiration();
			
			ClientTicket clientTicket = generateClientTicket(identity, service.serviceAddress, service.servicePort, sessionKey, expiration);
			ServiceTicket serviceTicket = generateServiceTicket(identity, service, sessionKey, expiration);
			
			String clientTicketCipher = Crypt.getInstance().encode(Crypt.getInstance().encryptMessage(clientKey, clientTicket.getJson().toString()));
			String serviceTicketCipher = Crypt.getInstance().encode(Crypt.getInstance().encryptMessage(service.secretKey, serviceTicket.getJson().toString()));
			
//			String clientTicketDecrypted = Crypt.getInstance().decryptMessage(clientKey, Crypt.getInstance().decode(clientTicketCipher));
//			System.out.println("Client key: " + Crypt.getInstance().stringFromSecretKey(clientKey));
			
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
	
	private ClientTicket generateClientTicket(String identity, String serviceAddress, int servicePort, SecretKey sessionKey, long expiration){
		ClientTicket ticket = new ClientTicket(Crypt.getInstance());
		ticket.identity = identity;
		ticket.serviceAddress = serviceAddress;
		ticket.servicePort = servicePort;
		ticket.sessionKey = sessionKey;
		ticket.expiration = expiration;
		
		return ticket;
	}
	
	private ServiceTicket generateServiceTicket(String clientIdentity, Service service, SecretKey sessionKey, long expiration){
		ServiceTicket ticket = new ServiceTicket(Crypt.getInstance());
		ticket.clientIdentity = clientIdentity;
		ticket.serviceIdentity = service.identity;
		ticket.sessionKey = sessionKey;
		ticket.expiration = expiration;
		
		return ticket;
	}
}
