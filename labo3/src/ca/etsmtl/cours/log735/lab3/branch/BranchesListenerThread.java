package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
				branch.addIncomingChannel(ois);
				System.out.println("BranchListener: Branch input stream added.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
