package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import ca.etsmtl.cours.log735.lab3.bank.Bank;
import ca.etsmtl.cours.log735.message.MoneyAmountRequestMessage;
import ca.etsmtl.cours.log735.message.TxnMessage;

/**
 * Cette classe notifie la branche des qu'une capture est terminee.
 * 
 * */
public class CaptureStateThread extends Observable implements Observer{
	
	private volatile String tempChannelsText;
	private volatile int totalCaptureMoneyAmount;
	private volatile int tempCaptureMoneyAmt;
	private volatile Branch branch;
	private volatile boolean keepCapturing;
	private CaptureRunner captureRunner;
	private HashMap<ObjectInputStream, Integer> channelTransactions;//to keep track of the transactions.
	
	public static final boolean START_CAPTURE = true, STOP_CAPTURE = false;
	
	public CaptureStateThread(Branch branch){
		this.branch = branch;
		addObserver(branch);
		keepCapturing = true;
		totalCaptureMoneyAmount = 0;
		tempCaptureMoneyAmt = 0;
		channelTransactions = new HashMap<ObjectInputStream, Integer>();
		captureRunner = new CaptureRunner(this);
		captureRunner.start();
	}
	
	/*
	 * Cette classe interne se charge de faire l'ecoute des canaux et de capturer
	 * les transactions. 
	 * **/
	class CaptureRunner extends Thread{

		private volatile boolean isAlreadyCapturing;
		private String captureText = "";
		private CaptureStateThread internalRef;
		private ArrayList<ChannelWatcherThread> watchers;
		
		public CaptureRunner(CaptureStateThread internalRef){
			isAlreadyCapturing = false;
			this.internalRef = internalRef;//only used for channel watcher
			watchers = new ArrayList<ChannelWatcherThread>();
		}
		
		@Override
		public void run(){
			System.out.println("Debut de l'enregistrement..");
			while(keepCapturing){
				if(!isAlreadyCapturing){
					System.out.println("Enregistrement des etats globaux initiaux");
					isAlreadyCapturing = true;
					//capture d'état initial de soi-meme
					totalCaptureMoneyAmount = branch.getInitialMoney();
					captureText = "Succursale #" + branch.getMyId() + " :" + branch.getCurrentMoney() + "$\n";
					//on recupere les montants initiaux de chaque autre succursale.
					for(UUID id : branch.getOutgoingChannelsByUUID().keySet()){
						boolean knowInitialMoneyAmt = false;
						//on regarde voir si on a le montant initial.
						for(UUID subId : branch.getBranchesMoneyAmtList().keySet()){
							if(id.equals(subId)){
								knowInitialMoneyAmt = true;
								break;
							}
						}
						//si on ne l'a pas on va le chercher.
						if(!knowInitialMoneyAmt){
							System.out.print("Attempting to request money amount to branch.. " + id);
							for(UUID tmpId : branch.getOutgoingChannelsByUUID().keySet()){
								if(tmpId.equals(id)){
									ObjectOutputStream tmpSendOOS = branch.getOutgoingChannelsByUUID().get(tmpId);
									try {
										tmpSendOOS.writeObject(new MoneyAmountRequestMessage(branch.getMyId()));
										System.out.println(" .. done ! Waiting for reponse..");
										boolean keepWaiting = true;
										while(keepWaiting){
											//wait for response..
											if (branch.getBranchesMoneyAmtList().containsKey(id)){
												keepWaiting = false;
												System.out.println(" got the response ..");
												break;
											}
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
									break;
								}
							}
						}
						//sinon on ajoute le montant initial de cette succursale.
						captureText += "Succursale #" + id + " :" + branch.getBranchesMoneyAmtList().get(id) + "$\n";
						totalCaptureMoneyAmount += branch.getBranchesMoneyAmtList().get(id);
					}//fin for pour toutes les succursales
					
					//demande de la somme d'argent total dans le systeme a la banque une fois
					Branch.BANK_LAST_KNOWN_TOTAL_AMOUNT = 0;
					new BankTotalAmountFetcherThread(branch).start();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else{
					//une fois les etats initiaux enregistres, on enregistre les canaux
					//mais on enregistre dans une variable temporaire, le temps de recevoir
					//le message de fin d'ecoute.
					tempChannelsText = "";
					tempCaptureMoneyAmt = 0;
					for(UUID curBranchId : branch.getIncomingChannelsByUUID().keySet()){
						ObjectInputStream tmpOIS = branch.getIncomingChannelsByUUID().get(curBranchId);
						//for each object input stream we add a channel watcher thread
						ChannelWatcherThread channelWatcher = new ChannelWatcherThread(tmpOIS);
						if(!watchers.contains(channelWatcher)){
							watchers.add(channelWatcher);
							channelWatcher.addObserver(internalRef);
							channelWatcher.startWatching();
						}
						else{
							//si les watchers sont deja ajoutes, on met a jout les canaux
							tempChannelsText = "";
							tempCaptureMoneyAmt = 0;
							//update temp capture transaction amount if we have new transaction values
							for(ObjectInputStream ois : channelTransactions.keySet()){
								for(UUID bId : branch.getIncomingChannelsByUUID().keySet()){
									if(branch.getIncomingChannelsByUUID().get(bId) == ois){
										tempChannelsText += "Canal S" + branch.getMyId() + " - S" + bId + ": " + channelTransactions.get(ois) + "$\n";
									}
								}
								tempCaptureMoneyAmt += channelTransactions.get(ois);//we get the transaction value associated to that stream
							}
						}
					}
					tempChannelsText += "Somme connue par la banque : " + Branch.BANK_LAST_KNOWN_TOTAL_AMOUNT + "$\n";
					tempChannelsText += "Somme detectee par la capture : " + (totalCaptureMoneyAmount + tempCaptureMoneyAmt) + "$\n";
					tempChannelsText += "ETAT GLOBAL " + (Branch.BANK_LAST_KNOWN_TOTAL_AMOUNT == (totalCaptureMoneyAmount + tempCaptureMoneyAmt) ? "COHERENT":"INCOHERENT (delta :" + (Branch.BANK_LAST_KNOWN_TOTAL_AMOUNT - (totalCaptureMoneyAmount + tempCaptureMoneyAmt)) + ")") + "\n";
				}
			}//fin while keepCapturing
			//on ferme les watchers
			System.out.println("Closing channel watchers...");
			for(int i = 0; i < watchers.size(); i++){
				watchers.get(i).stopWatching();
			}
			//une fois la capture terminee, on assemble le tout ensemble.
			captureText += tempChannelsText;
			System.out.println("Final capture for this branch : ");
			System.out.println(captureText);
			setChanged();
			System.out.println("CaptureStateRunner finished, notifying CapState.");
			notifyObservers(captureText);
			clearChanged();
		}
		
		public String getCaptureText(){
			return captureText;
		}
	}
	
	/**
	 * 
	 * @return true if this class' internal capture runner thread is capturing the state or not.
	 * 
	 * */
	public boolean isCapturing(){
		return keepCapturing;
	}
	
	/**
	 * 
	 * @param The capture mode state for this capture state thread. True to start capture, false to stop capturing.
	 * */
	public void setCaptureMode(boolean mode){
		keepCapturing = mode;
		if(keepCapturing){
			channelTransactions = new HashMap<ObjectInputStream, Integer>();
			captureRunner = new CaptureRunner(this);
			captureRunner.start();
		}
	}

	public String getCaptureText() {
		return captureRunner.getCaptureText();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		System.out.println("CapState notified");
		if(arg0 instanceof ChannelWatcherThread){
			//we are being notified that there are incoming transactions for the channel.
			if(keepCapturing){
				//if we're still suppose to update our stuff
				System.out.println("Received an updated transaction amount for a channel and still capturing, updating.");
				channelTransactions.put(((ChannelWatcherThread) arg0).getOIS(), ((TxnMessage) arg1).getAmount());
			}
			else{
				System.out.println("Received an updated transaction amount but stopped capturing, ignoring..");
			}
		}
	}
	
	
}
