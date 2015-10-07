package com.spinalcraft.authenticationserver;

import java.util.Base64;

import com.spinalcraft.easycrypt.EasyCrypt;

public class Crypt extends EasyCrypt{
	
	private static Crypt instance;
	
	private Crypt(){
		
	}
	
	public static Crypt getInstance(){
		if(instance == null)
			instance = new Crypt();
		return instance;
	}
	
	@Override
	public byte[] decode(String str){
		return Base64.getDecoder().decode(str);
	}
	
	@Override
	public String encode(byte[] bytes){
		return Base64.getEncoder().encodeToString(bytes);
	}
}
