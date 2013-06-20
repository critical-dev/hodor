package ca.etsmtl.cours.log735.lab3.bank;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

import ca.etsmtl.cours.log735.lab3.branch.Branch;
import ca.etsmtl.cours.log735.message.HelloMessage;


public class BranchUpdaterThread extends Thread{
	
	private Socket client;
	private HashMap<UUID, InetAddress> currentBranches;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	public BranchUpdaterThread(Socket client, HashMap<UUID, InetAddress> currentBranches){
		this.client = client;
		this.currentBranches = currentBranches;
	}
	
	@Override
	public void run(){
		try {
			InetAddress newBranchInetAddress = client.getInetAddress();
			UUID newBranchUUIDToAdd;
			
			System.out.println("Server discovered new client on InetAddress : " + newBranchInetAddress);
			
			ois = new ObjectInputStream(client.getInputStream());
			Object newBranchMessage;
			
			newBranchMessage = ois.readObject();
			//if the branch message is a hello message, we add it to the list of currentBranches,
			//then we notify that client of all the current clients.
			if(newBranchMessage instanceof HelloMessage){
				//retrieve the uuid from the HelloMessage
				newBranchUUIDToAdd = ((HelloMessage) newBranchMessage).getUuid();
				//add to list of current branches
				currentBranches.put(newBranchUUIDToAdd, newBranchInetAddress);
				//notify the newly added client first.
				oos = new ObjectOutputStream(client.getOutputStream());
				//pass the whole list
				oos.writeObject(currentBranches);
				
				
			}
		} catch (IOException e) {
			System.err.println("Server -> IOException occured : " + e.getCause());
		} catch (ClassNotFoundException e) {
			System.err.println("Server is unable to parse input stream object,");
		} finally{
			try {
				ois.close();
			} catch (IOException e) {}
		}
	}

}
