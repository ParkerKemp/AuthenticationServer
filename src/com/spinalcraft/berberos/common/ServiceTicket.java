package com.spinalcraft.berberos.common;

import javax.crypto.SecretKey;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.spinalcraft.berberos.authserver.Crypt;

public class ServiceTicket {
	public String clientIdentity;
	public String serviceIdentity;
	public long expiration;
	public SecretKey sessionKey;
	
	public static ServiceTicket fromCipher(String cipher, SecretKey secretKey){
		ServiceTicket ticket = new ServiceTicket();
		String json = Crypt.getInstance().decryptMessage(secretKey, cipher.getBytes());
		if(json == null){
			return null;
		}
		try{
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(json).getAsJsonObject();
			ticket.clientIdentity = obj.get("clientIdentity").getAsString();
			ticket.serviceIdentity = obj.get("serviceIdentity").getAsString();
			ticket.expiration = obj.get("expiration").getAsLong();
			ticket.sessionKey = Crypt.getInstance().loadSecretKey(obj.get("sessionKey").getAsString());
			return ticket;
		}catch(JsonParseException e){
			return null;
		}
	}
	
	public JsonObject getJson(){
		JsonObject obj = new JsonObject();
		obj.addProperty("clientIdentity", clientIdentity);
		obj.addProperty("serviceIdentity", serviceIdentity);
		obj.addProperty("expiration", expiration);
		obj.addProperty("secretKey", Crypt.getInstance().stringFromSecretKey(sessionKey));
		return obj;
	}
}
