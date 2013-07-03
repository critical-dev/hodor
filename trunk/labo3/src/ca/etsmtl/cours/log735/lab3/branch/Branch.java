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
import java.util.Observer;
import java.util.UUID;

import ca.etsmtl.cours.log735.lab3.bank.Bank;
import ca.etsmtl.cours.log735.message.HelloMessage;
import ca.etsmtl.cours.log735.message.TxnMessage;

public class Branch extends Observable implements Observer{
	
	public static final int BANK_PORT = 4343;
	public static final int BRANCHES_PORT = 4444;
	
	private int bankLastKnownTotalMoneyAmount = 0;
	
	private boolean isAlreadyCapturing;
	private String lastCaptureStateMessage = "";
	
	private int initialMoney;
	private int currentMoney;
	
	private List<UUID> peerIds = new LinkedList<UUID>();
	private UUID myId = UUID.randomUUID();

	private InetAddress bankIp;
	
	//for other branches
	private List<ObjectOutputStream> outgoingChannels = new LinkedList<ObjectOutputStream>();
	private HashMap<UUID, ObjectOutputStream> outgoingChannelsByUUID;
	private HashMap<UUID, ObjectInputStream> incomingChannelsByUUID;
	private HashMap<UUID, Integer> branchesMoneyAmtList;
	
	public Branch(int initialMoney, InetAddress bankIp) throws IOException {
		this.initialMoney = initialMoney;
		this.currentMoney = this.initialMoney;
		this.bankIp = bankIp;
		outgoingChannelsByUUID = new HashMap<UUID, ObjectOutputStream>();
		incomingChannelsByUUID = new HashMap<UUID, ObjectInputStream>();
		branchesMoneyAmtList = new HashMap<UUID, Integer>();
		
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
		notifyObservers("*************************\nGLOBAL STATE :\n Succursale d'origine de la capture : #" + myId + "\n" + lastCaptureStateMessage + "\n*************************\n");
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
		if (currentMoney > 0 && !outgoingChannels.isEmpty()) {
			ObjectOutputStream channel = outgoingChannels.get((int) (outgoingChannels.size() * Math.random()));
			int amount = (int) (currentMoney * Math.random());
			try {
				channel.writeObject(new TxnMessage(myId, amount));
				currentMoney -= amount;
				setChanged();
				notifyObservers("Sent " + amount + " [ " + currentMoney + "$]\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void recvMoney(UUID from, int amount) {
		currentMoney += amount;
		setChanged();
		notifyObservers("Received " + amount + "$ from " + from + " [ " + currentMoney + "$]\n");
	}

	public UUID getMyId() {
		return myId;
	}

	public int getInitialMoney() {
		return initialMoney;
	}

	public int getCurrentMoney() {
		return currentMoney;
	}

	public HashMap<UUID, Integer> getBranchesMoneyAmtList() {
		return branchesMoneyAmtList;
	}

	public void setBranchesMoneyAmtList(
			HashMap<UUID, Integer> branchesInitialMoneyAmtList) {
		this.branchesMoneyAmtList = branchesInitialMoneyAmtList;
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
	
	//needed to request total money amount to bank

	public InetAddress getBankIp() {
		return bankIp;
	}

	public void setBankIp(InetAddress bankIp) {
		this.bankIp = bankIp;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		System.out.println("Branch notified");
		if(arg0 instanceof CaptureStateThread){
			//it means we must update the GUI:
			System.out.println("Changing message.");
			lastCaptureStateMessage = (String) arg1;
			enforceDisplayCaptureState();
		}
	}

	public int getBankLastKnownTotalMoneyAmount() {
		return bankLastKnownTotalMoneyAmount;
	}

	public void setBankLastKnownTotalMoneyAmount(
			int bankLastKnownTotalMoneyAmount) {
		this.bankLastKnownTotalMoneyAmount = bankLastKnownTotalMoneyAmount;
	}
}
