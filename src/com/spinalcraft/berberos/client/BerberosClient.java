package com.spinalcraft.berberos.client;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class BerberosClient {
	public BerberosClient(String password){
		
	}
	
	private String getHash(String password){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");

			md.update(password.getBytes("UTF-8"));
			byte[] digest = md.digest();
			return new String(digest);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
