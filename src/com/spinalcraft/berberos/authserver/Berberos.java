package com.spinalcraft.berberos.authserver;

public class Berberos {
	public static void main(String[] args){
		Database.getInstance().init("Authentication");
		if(args.length > 0 && args[0].equals("getauth")){
			getAuth();
			System.exit(0);
		}
	}
	
	private static void getAuth(){
		String url = GetAuth.get();
		if(url != null){
			System.out.println(url);
		}
		else{
			System.err.println("Failed to create an auth URL");
		}
	}
}
