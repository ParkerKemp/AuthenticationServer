package com.spinalcraft.authenticationserver;

public class AuthenticationServer {
	public static void main(String[] args){
		Database.getInstance().init("Authentication");
	}
}
