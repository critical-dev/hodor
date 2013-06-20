package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

import ca.etsmtl.cours.log735.lab3.bank.Bank;

public class BankListenerThread extends Thread {
	
	private Branch branch;
	private ServerSocket sock;

	public BankListenerThread(Branch branch) {
		try {
			this.branch = branch;
			sock = new ServerSocket(Bank.PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Socket conn = sock.accept();
				ObjectInputStream ois = new ObjectInputStream(conn.getInputStream());
				Object input = ois.readObject();
				if (input instanceof HashMap) {
					branch.refreshBranchList((HashMap<UUID, InetAddress>) input);
				}
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
