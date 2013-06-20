package ca.etsmtl.cours.log735.lab3.bank;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

/**
 * Class Bank
 * Classe en charge de recevoir les connexions de succursales et
 * notifier celles-ci de l'ajout d'une nouvelle succursale sur le réseau.
 * */
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
		System.out.println("Bank is listening on port : " + PORT + " .. ");
		while(true){
			if(bankSocket != null){
				try {
					Socket newBranch = bankSocket.accept();
					System.out.println("New connexion received !");
					new BranchUpdaterThread(newBranch, currentBranches).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				System.err.println("Bank socket is null, aborting.");
				break;
			}
		}
	}
}
