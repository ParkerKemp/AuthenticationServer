package com.spinalcraft.berberos.common;

import javax.crypto.SecretKey;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.spinalcraft.berberos.authserver.Crypt;

public class Ticket {
	public String identity;
	public long expiration;
	public SecretKey sessionKey;
	
	public static Ticket fromCipher(String cipher, SecretKey secretKey){
		Ticket ticket = new Ticket();
		String json = Crypt.getInstance().decryptMessage(secretKey, cipher.getBytes());
		if(json == null){
			return null;
		}
		try{
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(json).getAsJsonObject();
			ticket.identity = obj.get("identity").getAsString();
			ticket.expiration = obj.get("expiration").getAsLong();
			ticket.sessionKey = Crypt.getInstance().loadSecretKey(obj.get("sessionKey").getAsString());
			return ticket;
		}catch(JsonParseException e){
			return null;
		}
	}
	
	public JsonObject getJson(){
		JsonObject obj = new JsonObject();
		obj.addProperty("identity", identity);
		obj.addProperty("expiration", expiration);
		obj.addProperty("secretKey", Crypt.getInstance().stringFromSecretKey(sessionKey));
		return obj;
	}
}
