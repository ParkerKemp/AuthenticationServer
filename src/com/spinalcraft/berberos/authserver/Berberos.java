package com.spinalcraft.berberos.authserver;

public class Berberos {
	public static void main(String[] args){
		Database.getInstance().init("Authentication");
		if(args.length > 0)
			switch(args[0]){
			case "getauthkey":
				getAuth();
				System.exit(0);	
				break;
			case "getservicekey":
				getServiceKey();
				System.exit(0);
				break;
			}
		
		(new Thread(new Listener())).start();
	}
	
	private static void getServiceKey(){
		String key = GetServiceKey.get();
		if(key != null){
			System.out.println(key);
		}
		else{
			System.err.println("Failed to create a service key");
		}
	}
	
	private static void getAuth(){
		String url = GetAuthKey.get();
		if(url != null){
			System.out.println(url);
		}
		else{
			System.err.println("Failed to create an auth URL");
		}
	}
}
