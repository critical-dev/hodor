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
import ca.etsmtl.cours.log735.message.StateSyncStartMessage;
import ca.etsmtl.cours.log735.message.StateSyncStopMessage;
import ca.etsmtl.cours.log735.message.TxnMessage;

public class Branch extends Observable implements Observer{
	
	public static final int BANK_PORT = 4343;
	public static final int BRANCHES_PORT = 4444;
	
	private volatile int bankLastKnownTotalMoneyAmount = 0;
	
	private boolean isRequestingCapture;
	private String lastCaptureStateMessageHeader = "";
	private String lastCaptureStateMessage = "";
	
	private int initialMoney;
	private int currentMoney;
	
	private List<UUID> peerIds = new LinkedList<UUID>();
	private int nbStateAnswersReceived = 0;
	private UUID myId = UUID.randomUUID();

	private InetAddress bankIp;
	
	//for other branches
	private List<ObjectOutputStream> outgoingChannels = new LinkedList<ObjectOutputStream>();
	private HashMap<UUID, ObjectOutputStream> outgoingChannelsByUUID;
	private HashMap<UUID, ObjectInputStream> incomingChannelsByUUID;
	private HashMap<UUID, Integer> branchesMoneyAmtList;
	private volatile HashMap<Long, HashMap<UUID, Integer>> transactions;
	private CaptureStateThread myCaptureStateThread;
	private List<UUID> capStateRequestors;
	
	public Branch(int initialMoney, InetAddress bankIp) throws IOException {
		this.initialMoney = initialMoney;
		this.currentMoney = this.initialMoney;
		this.bankIp = bankIp;
		myCaptureStateThread = new CaptureStateThread(this);
		capStateRequestors = new ArrayList<UUID>();
		outgoingChannelsByUUID = new HashMap<UUID, ObjectOutputStream>();
		incomingChannelsByUUID = new HashMap<UUID, ObjectInputStream>();
		branchesMoneyAmtList = new HashMap<UUID, Integer>();
		transactions = new HashMap<Long, HashMap<UUID,Integer>>();
		//this order is very important, keep the bank listener thread first
		//it basically enforces that branches are mutually connected before any
		//other in-branch treatment can be performed.
		new BankListenerThread(this).start();
		new BranchesListenerThread(this).start();
		isRequestingCapture = false;
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
		if(!lastCaptureStateMessage.contains("Canal")){
			lastCaptureStateMessage += "[No data in channels during capture.]";
		}
		notifyObservers(lastCaptureStateMessageHeader + lastCaptureStateMessage + "\n*************************\n");
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
			System.out.println("Adding new oos");
			outgoingChannelsByUUID.put(id,oos);
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			//incomingChannelsByUUID.put(id,ois);
			//System.out.println("Adding new ois [total:" + incomingChannelsByUUID.size() + "]");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void captureState() throws IOException, InterruptedException{
		setRequestingCapture(true);
		System.out.println(getMyId() + " initiating a global capture.");
		getMyCaptureStateThread().setCaptureMode(CaptureStateThread.START_CAPTURE);
		for(UUID id : getOutgoingChannelsByUUID().keySet()){						
			ObjectOutputStream oos = getOutgoingChannelsByUUID().get(id);
			//System.out.println("I AM " + branch.getMyId());
			System.out.println("Sending START capture message request to id : " + id);
			oos.writeObject(new StateSyncStartMessage(getMyId()));//request a state capture
			Thread.sleep(6000); //sleep again
			System.out.println("Sending STOP capture message request to id : " + id);
			oos.writeObject(new StateSyncStopMessage(getMyId()));//request the previous state capture's response
		}
		getMyCaptureStateThread().setCaptureMode(CaptureStateThread.STOP_CAPTURE);
	}

	public boolean isRequestingCapture() {
		return isRequestingCapture;
	}

	public void setRequestingCapture(boolean isRequestingCapture) {
		this.isRequestingCapture = isRequestingCapture;
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
		HashMap<UUID, Integer> transaction = new HashMap<UUID, Integer>();
		transaction.put(from, new Integer(amount));
		System.out.println("Added transaction");
		transactions.put(System.currentTimeMillis(), transaction);
		setChanged();
		notifyObservers("Received " + amount + "$ from " + from + " [ " + currentMoney + "$]\n");
	}

	public synchronized UUID getMyId() {
		return myId;
	}

	public int getInitialMoney() {
		return initialMoney;
	}

	public int getCurrentMoney() {
		return currentMoney;
	}

	public synchronized HashMap<UUID, Integer> getBranchesMoneyAmtList() {
		return branchesMoneyAmtList;
	}

	public void setBranchesMoneyAmtList(
			HashMap<UUID, Integer> branchesInitialMoneyAmtList) {
		this.branchesMoneyAmtList = branchesInitialMoneyAmtList;
	}

	public synchronized List<ObjectOutputStream> getOutgoingChannels() {
		return outgoingChannels;
	}

	public synchronized HashMap<UUID, ObjectOutputStream> getOutgoingChannelsByUUID() {
		return outgoingChannelsByUUID;
	}

	/*public HashMap<UUID, ObjectInputStream> getIncomingChannelsByUUID() {
		return incomingChannelsByUUID;
	}*/

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

	public List<UUID> getPeerIds() {
		return peerIds;
	}

	public void setBankIp(InetAddress bankIp) {
		this.bankIp = bankIp;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		System.out.println("Branch notified");
		if(arg0 instanceof CaptureStateThread){
			//it means we must update the GUI:
			System.out.println("Updating branch capture message due to CapStateThread finished.");
			mergeCaptureMessageInfo((String)arg1);
			System.out.println("lastCaptureMessage is " + lastCaptureStateMessage);
			if(isRequestingCapture){
				enforceDisplayCaptureState();
				isRequestingCapture = false;
			}
		}
	}
	
	public void mergeCaptureMessageInfo(String otherBranchCaptureMessage){
		if(otherBranchCaptureMessage.contains("Canal")){
			if(!lastCaptureStateMessage.contains("Somme")){
				this.lastCaptureStateMessage += otherBranchCaptureMessage;
			}
			else{
				otherBranchCaptureMessage = otherBranchCaptureMessage.substring(otherBranchCaptureMessage.indexOf("Canal"), otherBranchCaptureMessage.indexOf("$",otherBranchCaptureMessage.indexOf("Canal")));
				this.lastCaptureStateMessage += otherBranchCaptureMessage;
			}
		}
		else if(!lastCaptureStateMessage.contains("Somme")){
			this.lastCaptureStateMessage += otherBranchCaptureMessage;
		}
	}

	public synchronized int getBankLastKnownTotalMoneyAmount() {
		return bankLastKnownTotalMoneyAmount;
	}

	public synchronized void setBankLastKnownTotalMoneyAmount(
			int bankLastKnownTotalMoneyAmount) {
		this.bankLastKnownTotalMoneyAmount = bankLastKnownTotalMoneyAmount;
		System.out.println("New bank money amount set.");
	}

	public synchronized HashMap<Long, HashMap<UUID, Integer>> getTransactions() {
		return transactions;
	}

	public void setTransactions(HashMap<Long, HashMap<UUID, Integer>> transactions) {
		this.transactions = transactions;
	}

	public CaptureStateThread getMyCaptureStateThread() {
		return myCaptureStateThread;
	}

	public void setMyCaptureStateThread(CaptureStateThread myCaptureStateThread) {
		this.myCaptureStateThread = myCaptureStateThread;
	}

	public List<UUID> getCapStateRequestors() {
		return capStateRequestors;
	}

	public void setCapStateRequestors(List<UUID> capStateRequestors) {
		this.capStateRequestors = capStateRequestors;
	}

	public int getNbStateAnswersReceived() {
		return nbStateAnswersReceived;
	}

	public void setNbStateAnswersReceived(int nbStateAnswersReceived) {
		this.nbStateAnswersReceived = nbStateAnswersReceived;
	}

	public String getLastCaptureStateMessageHeader() {
		return lastCaptureStateMessageHeader;
	}

	public void setLastCaptureStateMessageHeader(
			String lastCaptureStateMessageHeader) {
		this.lastCaptureStateMessageHeader = "*************************\nGLOBAL STATE :\n Succursale d'origine de la capture : #" + myId + "\n";
		this.lastCaptureStateMessageHeader += lastCaptureStateMessageHeader + "\n";
	}
}
