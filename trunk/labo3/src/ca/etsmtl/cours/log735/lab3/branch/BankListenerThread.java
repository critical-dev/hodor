package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

import ca.etsmtl.cours.log735.message.TotalMoneyResponseMessage;

public class BankListenerThread extends Thread {

	private Branch branch;
	private ServerSocket sock;

	public BankListenerThread(Branch branch) {
		this.branch = branch;
	}

	@Override
	public void run() {
		System.out.println("BankListener: Listening for connection from bank...");
		try {
			sock = new ServerSocket(Branch.BANK_PORT);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true) {
			try {
				Socket conn = sock.accept();
				System.out.println("BankListener: Bank connected.");
				ObjectInputStream ois = new ObjectInputStream(conn.getInputStream());
				Object input = ois.readObject();
				System.out.println("BankListener: Received message from bank.");
				if (input instanceof HashMap) {
					branch.refreshBranchList((HashMap<UUID, InetAddress>) input);
					System.out.println("BankListener: Processed branch list update.");
					int updatedMoneyAmount = ((TotalMoneyResponseMessage) ois.readObject()).getAmount();
					System.out.println("BankListener: Got Bank updated money amount total.");
					branch.setBankLastKnownTotalMoneyAmount(updatedMoneyAmount);
				}
				else if(input instanceof TotalMoneyResponseMessage){
					
					branch.setBankLastKnownTotalMoneyAmount(((TotalMoneyResponseMessage) input).getAmount());
				}
				else{
					System.out.println("Unrecognized message sent...");
				}
				ois.close();
				conn.close();
				System.out.println("BankListener: Connection closed.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
