package ca.etsmtl.cours.log735.lab3.bank;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Observable;
import java.util.UUID;

/**
 * Class Bank
 * Classe en charge de recevoir les connexions de succursales et
 * notifier celles-ci de l'ajout d'une nouvelle succursale sur le réseau.
 * */
public class Bank extends Observable{
	
	/**
	 * Liste des succursales connues de la banque
	 * */
	private HashMap<UUID, InetAddress> currentBranches;
	private ServerSocket bankSocket;
	private BankListener bankListener;
	
	public static int BANK_TOTAL_MONEY_IN_THE_SYSTEM;
	
	/**
	 * Port utilise par la banque pour ecouter ET se connecter aux succursales.
	 * */
	public static final int PORT = 4242;
	
	public Bank(){
		BANK_TOTAL_MONEY_IN_THE_SYSTEM = 0;
		currentBranches = new HashMap<UUID, InetAddress>();
		try {
			bankSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bankListener = new BankListener();
	}
	
	public void start(){
		bankListener.start();
	}
	
	public BankListener getBankListener() {
		return bankListener;
	}

	class BankListener extends Thread{
				
		@Override
		public void run(){
			System.out.println("Bank is listening on port : " + PORT + " .. ");
			while(true){
				if(bankSocket != null){
					try {
						Socket newBranch = bankSocket.accept();
						System.out.println("New connexion received !");
						new BranchUpdaterThread(newBranch, currentBranches).start();
						setChanged();
						notifyObservers();
						clearChanged();
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

	public HashMap<UUID, InetAddress> getCurrentBranches() {
		return currentBranches;
	}
}
