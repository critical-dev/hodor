package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

import ca.etsmtl.cours.log735.lab3.bank.Bank;
import ca.etsmtl.cours.log735.message.HelloMessage;

public class Branch extends Observable {
	
	public static final int BANK_PORT = 4343;
	public static final int BRANCHES_PORT = 4444;
	
	private boolean isCapturing;
	
	private List<UUID> peerIds = new LinkedList<UUID>();
	private UUID myId = UUID.randomUUID();
	
	private List<ObjectInputStream> incomingChannels = new LinkedList<ObjectInputStream>();
	private List<ObjectOutputStream> outgoingChannels = new LinkedList<ObjectOutputStream>();
	
	public Branch(int initialMoney, InetAddress bankIp) throws IOException {
		new BankListenerThread(this).start();
		new BranchesListenerThread(this).start();
		isCapturing = false;
		Socket sock = new Socket(bankIp, Bank.PORT);
		System.out.println("Branch: Connected to bank.");
		ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
		oos.writeObject(new HelloMessage(myId, initialMoney));
		System.out.println("Branch: Hello sent to bank.");
		oos.close();
		sock.close();
	}

	public void refreshBranchList(HashMap<UUID, InetAddress> branchList) {
		for (UUID id: branchList.keySet()) {
			if (!peerIds.contains(id) && id.compareTo(myId) != 0) {
				connect(branchList.get(id));
				this.peerIds.add(id);
				System.out.println("Branch: Added " + id + " to peer list and connected.");
			}
		}
	}

	/**
	 * 		captureState
	 * @goal When invoked, checks whether this branch is capturing its state or not.
	 * If it already is, sets the isCapturing flag to false and stops the current capture.
	 * Otherwise, sets the isCapturing flag to true and starts the current capture.
	 * */
	public void captureState() {
		// TODO Auto-generated method stub
		
	}

	public void addIncomingChannel(ObjectInputStream ois) {
		incomingChannels.add(ois);
	}
	
	private void addOutgoingChannel(ObjectOutputStream oos) {
		outgoingChannels.add(oos);
	}
	
	private void connect(InetAddress peer) {
		try {
			Socket sock = new Socket(peer, Branch.BRANCHES_PORT);
			addOutgoingChannel(new ObjectOutputStream(sock.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isCapturing() {
		return isCapturing;
	}

	public void setCapturing(boolean isCapturing) {
		this.isCapturing = isCapturing;
	}
}