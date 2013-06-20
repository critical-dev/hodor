package ca.etsmtl.cours.log735.lab3.bank;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

import ca.etsmtl.cours.log735.branch.Branch;


public class BranchUpdaterThread extends Thread{
	
	private Socket client;
	private HashMap<UUID, InetAddress> currentBranches;
	private ObjectInputStream ois;
	
	public BranchUpdaterThread(Socket client, HashMap<UUID, InetAddress> currentBranches){
		this.client = client;
		this.currentBranches = currentBranches;
	}
	
	@Override
	public void run(){
		try {
			ois = new ObjectInputStream(client.getInputStream());
			Branch newBranch;
			while((newBranch = (Branch) ois.readObject()) != null){
				
			}
		} catch (IOException e) {
			System.err.println();
		} catch (ClassNotFoundException e) {
			System.err.println("Server is unable to parse input stream object, aborting.");
		} finally{
			try {
				ois.close();
			} catch (IOException e) {}
		}
	}

}
