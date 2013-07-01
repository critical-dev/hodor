package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

public class BankListenerThread extends Thread {
	
	private Branch branch;

	public BankListenerThread(Branch branch) {
		this.branch = branch;
	}
	
	@Override
	public void run() {
		try {
			ServerSocket sock = new ServerSocket(Branch.BANK_PORT);
			System.out.println("Listening for connection from bank...");
			while (true) {
				Socket conn = sock.accept();
				System.out.println("Bank connected.");
				ObjectInputStream ois = new ObjectInputStream(conn.getInputStream());
				Object input = ois.readObject();
				System.out.println("Received message from bank.");
				if (input instanceof HashMap) {
					branch.refreshBranchList((HashMap<UUID, InetAddress>) input);
					System.out.println("Processed branch list update.");
				}
				ois.close();
				conn.close();
				System.out.println("Connection closed.");
			}
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			// TODO Auto-generated catch block
			cnfe.printStackTrace();
		}
	}
}
