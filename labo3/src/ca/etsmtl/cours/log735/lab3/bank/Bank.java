package ca.etsmtl.cours.log735.lab3.bank;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.UUID;

public class Bank extends Thread{
	
	private HashMap<UUID, InetAddress> currentBranches;
	private ServerSocket bankSocket;
	
	public static final int PORT = 4242;
	
	public Bank(){
		currentBranches = new HashMap<UUID, InetAddress>();
		try {
			bankSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		while(true){
			
		}
	}
}
