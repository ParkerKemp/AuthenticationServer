package com.spinalcraft.authenticationserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable {
	
	@Override
	public void run(){
		ServerSocket socket = null;
		Socket conn = null;
//		Database db = new Database("Development");
		try {
			socket = new ServerSocket(9494);
			while(true){
				conn = socket.accept();
				new Thread(new ClientHandler(conn)).start();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
