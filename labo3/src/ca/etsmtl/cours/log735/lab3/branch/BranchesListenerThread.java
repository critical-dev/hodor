package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BranchesListenerThread extends Thread {
	
	private Branch branch;
	
	public BranchesListenerThread(Branch branch) {
		this.branch = branch;
	}

	@Override
	public void run() {
		try {
			ServerSocket sock = new ServerSocket(Branch.BRANCHES_PORT);
			System.out.println("Listening for connection from branches...");
			while (true) {
				Socket conn = sock.accept();
				System.out.println("Branch connected.");
				ObjectInputStream ois = new ObjectInputStream(conn.getInputStream());
				branch.addIncomingChannel(ois);
				System.out.println("Branch input stream added.");
			}
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
	}
}
