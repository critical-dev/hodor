package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

import ca.etsmtl.cours.log735.lab3.bank.Bank;
import ca.etsmtl.cours.log735.message.HelloMessage;
import ca.etsmtl.cours.log735.message.TxnMessage;

public class Branch extends Observable {
	
	public static final int BANK_PORT = 4343;
	public static final int BRANCHES_PORT = 4444;
	
	private boolean isAlreadyCapturing;
	private String lastCaptureStateMessage = "";
	
	private int initialMoney;
	
	private List<UUID> peerIds = new LinkedList<UUID>();
	private UUID myId = UUID.randomUUID();
	
	private List<ObjectOutputStream> outgoingChannels = new LinkedList<ObjectOutputStream>();
	
	private HashMap<UUID, ObjectOutputStream> outgoingChannelsByUUID;
	private HashMap<UUID, ObjectInputStream> incomingChannelsByUUID;
	private HashMap<UUID, Integer> branchesInitialMoneyAmtList;
	
	public Branch(int initialMoney, InetAddress bankIp) throws IOException {
		this.initialMoney = initialMoney;
		outgoingChannelsByUUID = new HashMap<UUID, ObjectOutputStream>();
		incomingChannelsByUUID = new HashMap<UUID, ObjectInputStream>();
		branchesInitialMoneyAmtList = new HashMap<UUID, Integer>();
		
		//this order is very important, keep the bank listener thread first
		//it basically enforces that branches are mutually connected before any
		//other in-branch treatment can be performed.
		new BankListenerThread(this).start();
		new BranchesListenerThread(this).start();
		isAlreadyCapturing = false;
		Socket sock = new Socket(bankIp, Bank.PORT);
		System.out.println("Branch: Connected to bank.");
		ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
		oos.writeObject(new HelloMessage(myId, initialMoney));
		System.out.println("Branch: Hello sent to bank.");
		oos.close();
		sock.close();
		new SendMoneyThread(this).start();
		//start new random capture thread
		new SendCaptureThread(this).start();
	}

	public void refreshBranchList(HashMap<UUID, InetAddress> branchList) {
		for (UUID id: branchList.keySet()) {
			if (!peerIds.contains(id) && id.compareTo(myId) != 0) {
				connect(branchList.get(id),id);
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
	public void enforceDisplayCaptureState() {
		setChanged();
		notifyObservers(lastCaptureStateMessage);
		clearChanged();
	}
		
	private void addOutgoingChannel(ObjectOutputStream oos) {
		outgoingChannels.add(oos);
	}
	
	private void connect(InetAddress peer, UUID id) {
		try {
			Socket sock = new Socket(peer, Branch.BRANCHES_PORT);
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			addOutgoingChannel(oos);
			outgoingChannelsByUUID.put(id,oos);
			incomingChannelsByUUID.put(id, new ObjectInputStream(sock.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isCapturing() {
		return isAlreadyCapturing;
	}

	public void setCapturing(boolean isCapturing) {
		this.isAlreadyCapturing = isCapturing;
	}
	
	public void sendMoney() {
		if (initialMoney > 0 && !outgoingChannels.isEmpty()) {
			ObjectOutputStream channel = outgoingChannels.get((int) (outgoingChannels.size() * Math.random()));
			int amount = (int) (initialMoney * Math.random());
			try {
				channel.writeObject(new TxnMessage(myId, amount));
				initialMoney -= amount;
				setChanged();
				notifyObservers("Sent " + amount + " [ " + initialMoney + "$]\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void recvMoney(UUID from, int amount) {
		initialMoney += amount;
		setChanged();
		notifyObservers("Received " + amount + "$ from " + from + " [ " + initialMoney + "$]\n");
	}

	public UUID getMyId() {
		return myId;
	}

	public int getInitialMoney() {
		return initialMoney;
	}

	public HashMap<UUID, Integer> getBranchesInitialMoneyAmtList() {
		return branchesInitialMoneyAmtList;
	}

	public void setBranchesInitialMoneyAmtList(
			HashMap<UUID, Integer> branchesInitialMoneyAmtList) {
		this.branchesInitialMoneyAmtList = branchesInitialMoneyAmtList;
	}

	public List<ObjectOutputStream> getOutgoingChannels() {
		return outgoingChannels;
	}

	public HashMap<UUID, ObjectOutputStream> getOutgoingChannelsByUUID() {
		return outgoingChannelsByUUID;
	}

	public HashMap<UUID, ObjectInputStream> getIncomingChannelsByUUID() {
		return incomingChannelsByUUID;
	}

	public String getLastCaptureStateMessage() {
		return lastCaptureStateMessage;
	}

	public void setLastCaptureStateMessage(String lastCaptureStateMessage) {
		this.lastCaptureStateMessage = lastCaptureStateMessage;
	}
}
