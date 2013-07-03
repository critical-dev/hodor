package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

import ca.etsmtl.cours.log735.message.InitialMoneyRequestMessage;

public class BranchesListenerThread extends Thread {
	
	private Branch branch;
	private ServerSocket sock;
	
	public BranchesListenerThread(Branch branch) {
		this.branch = branch;
		try {
			sock = new ServerSocket(Branch.BRANCHES_PORT);
			System.out.println("BranchListener: Listening for connection from branches...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {	
		while (true) {
			try {
				Socket conn = sock.accept();
				System.out.println("BranchListener: branch connected.");
				ObjectInputStream ois = new ObjectInputStream(conn.getInputStream());
				branch.setBankLastKnownTotalMoneyAmount(branch.getInitialMoney());
				//we request a capture but first we need the updated bank total
				for(UUID id : branch.getOutgoingChannelsByUUID().keySet()){						
					ObjectOutputStream oos = branch.getOutgoingChannelsByUUID().get(id);
					//System.out.println("I AM " + branch.getMyId());
					System.out.println("Sending initial money message request to id : " + id);
					oos.writeObject(new InitialMoneyRequestMessage(branch.getMyId()));
				}
				new TxnListenerThread(branch, ois).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
