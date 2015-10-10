package com.spinalcraft.authenticationserver;

import java.net.Socket;
import java.sql.SQLException;

public class RegistrationRequest {
	private String accessKey;
	private Socket socket;
	
	public RegistrationRequest(String accessKey, Socket socket){
		this.accessKey = accessKey;
		this.socket = socket;
	}
	
	public void process(){
		try {
			Actor actor = Actor.getFromUnclaimedAccessKey(accessKey);
			if(actor == null)
				return;
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
