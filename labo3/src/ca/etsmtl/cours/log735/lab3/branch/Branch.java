package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

import ca.etsmtl.cours.log735.lab3.bank.Bank;
import ca.etsmtl.cours.log735.message.HelloMessage;

public class Branch {
	
	private UUID branchId = UUID.randomUUID();
	private BankListenerThread blt;
	
	public Branch(int initialMoney, InetAddress bankIp) {
		try {
			Socket sock = new Socket(bankIp, Bank.PORT);
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeObject(new HelloMessage(branchId, initialMoney));
			blt = new BankListenerThread(this);
			blt.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String argv[]) {
		
	}

	public void refreshBranchList(HashMap<UUID, InetAddress> branchList) {
		// TODO Auto-generated method stub
		
	}
}