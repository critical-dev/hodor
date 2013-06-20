package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Observable;
import java.util.UUID;

import ca.etsmtl.cours.log735.lab3.bank.Bank;
import ca.etsmtl.cours.log735.message.HelloMessage;

public class Branch extends Observable {
	
	public static final int PORT = 4343;
	
	private UUID branchId = UUID.randomUUID();
	private BankListenerThread blt;
	
	public Branch(int initialMoney, InetAddress bankIp) throws IOException {
		Socket sock = new Socket(bankIp, Bank.PORT);
		ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
		oos.writeObject(new HelloMessage(branchId, initialMoney));
		blt = new BankListenerThread(this);
		blt.start();
	}

	public void refreshBranchList(HashMap<UUID, InetAddress> branchList) {
		// TODO Auto-generated method stub
		
	}

	public void captureState() {
		// TODO Auto-generated method stub
		
	}
}
