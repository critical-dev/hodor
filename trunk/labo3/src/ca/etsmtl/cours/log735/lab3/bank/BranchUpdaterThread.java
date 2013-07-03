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
import ca.etsmtl.cours.log735.message.TotalMoneyRequestMessage;
import ca.etsmtl.cours.log735.message.TotalMoneyResponseMessage;

/**
 * 	Fil d'exécution créé lorsque la banque (Bank) reçoit une nouvelle connexion.
 * Lit un objet d'un outputstream (instance de la classe HelloMesage et notifie la
 * succursale des sucursales présentement connues).
 * 
 * */
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
				newBranchUUIDToAdd = ((HelloMessage) newBranchMessage).getBranchId();
				Bank.BANK_TOTAL_MONEY_IN_THE_SYSTEM += ((HelloMessage) newBranchMessage).getInitialMoney();
				//add to list of current branches
				/**
				 * BANQUE-01 : 
				 * La Banque doit accepter la connexion réseau d'une Succursale et 
				 * l'intégrer  à sa liste de Succursales.
				 * */
				currentBranches.put(newBranchUUIDToAdd, newBranchInetAddress);
				System.out.println("Adding new branch UUID : " + newBranchUUIDToAdd + " with initial money : " + ((HelloMessage) newBranchMessage).getInitialMoney());
				client.close();//close the new branch's connexion
				
				System.out.println("Notified " + newBranchUUIDToAdd + " of other branches.");
				//notify all other branches
				for(UUID branchId : currentBranches.keySet()){
					System.out.println("Notifying other branch .. " + branchId);
					Socket client = new Socket(currentBranches.get(branchId), Branch.BANK_PORT);
					oos = new ObjectOutputStream(client.getOutputStream());
					oos.writeObject(currentBranches);
					oos.writeObject(new Integer(Bank.BANK_TOTAL_MONEY_IN_THE_SYSTEM));
					oos.close();
					client.close();
				}
			}
			else if(newBranchMessage instanceof TotalMoneyRequestMessage){
				System.out.println("Bank received request for total money in the system..");
				for(UUID branchId : currentBranches.keySet()){
					if(branchId.equals(((TotalMoneyRequestMessage) newBranchMessage).getFrom())){
						System.out.println("Notifying branch " + branchId + " of total money in the system : " + Bank.BANK_TOTAL_MONEY_IN_THE_SYSTEM);
						Socket client = new Socket(currentBranches.get(branchId), Branch.BANK_PORT);
						oos = new ObjectOutputStream(client.getOutputStream());
						oos.writeObject(new TotalMoneyResponseMessage(Bank.BANK_TOTAL_MONEY_IN_THE_SYSTEM));
						oos.close();
						client.close();
						break;
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Server -> IOException occured : " + e.getCause());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Server is unable to parse input stream object,");
			e.printStackTrace();
		} finally{
			try {
				ois.close();
			} catch (IOException e) {}
		}
	}

}
