package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

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
	//private HashMap<ObjectInputStream, String> channelTransactions;//to keep track of the transactions.
	private List<UUID> idsToTrack;
	private List<HashMap<UUID, Integer>> transactionsOfIds;
	
	public static final boolean START_CAPTURE = true, STOP_CAPTURE = false;
	
	public CaptureStateThread(Branch branch){
		this.branch = branch;
		idsToTrack = new ArrayList<UUID>();
		transactionsOfIds = new ArrayList<HashMap<UUID,Integer>>();
		addObserver(branch);
		keepCapturing = true;
		totalCaptureMoneyAmount = 0;
		tempCaptureMoneyAmt = 0;
		//channelTransactions = new HashMap<ObjectInputStream, String>();
		captureRunner = new CaptureRunner(this);
		//captureRunner.start();
	}
	
	/*
	 * Cette classe interne se charge de faire l'ecoute des canaux et de capturer
	 * les transactions. 
	 * **/
	class CaptureRunner extends Thread{

		private volatile boolean isAlreadyCapturing;
		private String captureRunnerTxnText = "";
		private CaptureStateThread internalRef;
		//private ArrayList<ChannelWatcherThread> watchers;
		private Long currentTime;
		
		public CaptureRunner(CaptureStateThread internalRef){
			isAlreadyCapturing = false;
			this.internalRef = internalRef;//only used for channel watcher
			//watchers = new ArrayList<ChannelWatcherThread>();
			currentTime = System.currentTimeMillis();
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
					captureRunnerTxnText = "Succursale #" + branch.getMyId() + " :" + branch.getCurrentMoney() + "$\n";
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
						captureRunnerTxnText += "Succursale #" + id + " :" + branch.getBranchesMoneyAmtList().get(id) + "$\n";
						branch.setLastCaptureStateMessageHeader(captureRunnerTxnText);
						totalCaptureMoneyAmount += branch.getBranchesMoneyAmtList().get(id);
					}//fin for pour toutes les succursales
					
				}
				else{
					//une fois les etats initiaux enregistres, on enregistre les canaux
					//mais on enregistre dans une variable temporaire, le temps de recevoir
					//le message de fin d'ecoute.
					tempChannelsText = "";
					tempCaptureMoneyAmt = 0;
					//System.out.println("Number transactions : " + branch.getTransactions().size());
					for(Long txnTime : branch.getTransactions().keySet()){
						if(txnTime > currentTime){
							HashMap<UUID, Integer> transaction = branch.getTransactions().get(txnTime);
							//only one element but easier this way:
							for(UUID id : transaction.keySet()){
								if(!idsToTrack.contains(id)){
									idsToTrack.add(id);
									transactionsOfIds.add(transaction);
									//get all transactions for the capture time
								}
								else{
									for(int i = 0; i < transactionsOfIds.size(); i++){
										if(transactionsOfIds.get(i).containsKey(id)){
											transactionsOfIds.remove(i);
											break;
										}
									}
								}
							}
						}
					}
					
					for(int i = 0; i < transactionsOfIds.size(); i++){
						for(UUID id : transactionsOfIds.get(i).keySet()){
							System.out.println("Updating transaction channels..");
							//only one each time
							tempChannelsText += "Canal S" + branch.getMyId() + " - S" + id + ": " + transactionsOfIds.get(i).get(id) + "$\n";
							tempCaptureMoneyAmt += transactionsOfIds.get(i).get(id);
						}
					}

					//tempCaptureMoneyAmt += Integer.parseInt(channelTransactions.get(ois).split("##")[1]);//we get the transaction value associated to that stream
			
					tempChannelsText += "Somme connue par la banque : " + branch.getBankLastKnownTotalMoneyAmount() + "$\n";
					tempChannelsText += "Somme detectee par la capture : " + (totalCaptureMoneyAmount + tempCaptureMoneyAmt) + "$\n";
					tempChannelsText += "ETAT GLOBAL " + (branch.getBankLastKnownTotalMoneyAmount() == (totalCaptureMoneyAmount + tempCaptureMoneyAmt) ? "COHERENT":"INCOHERENT (delta :" + (branch.getBankLastKnownTotalMoneyAmount() - (totalCaptureMoneyAmount + tempCaptureMoneyAmt)) + ")") + "\n";
					
					System.out.println(tempChannelsText);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}//fin while keepCapturing
			
			//une fois la capture terminee, on assemble le tout ensemble.
			captureRunnerTxnText = tempChannelsText;
			System.out.println("Final capture for this branch : ");
			System.out.println(captureRunnerTxnText);
			setChanged();
			System.out.println("CaptureStateRunner finished, notifying Branch.");
			notifyObservers(captureRunnerTxnText);
			clearChanged();
		}
				
		public String getCaptureText(){
			return captureRunnerTxnText;
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
			System.out.println("Resetting capture state thread for " + branch.getMyId());
			totalCaptureMoneyAmount = 0;
			tempCaptureMoneyAmt = 0;
			idsToTrack = new ArrayList<UUID>();
			transactionsOfIds = new ArrayList<HashMap<UUID,Integer>>();
			//channelTransactions = new HashMap<ObjectInputStream, String>();
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
		/*if(arg0 instanceof ChannelWatcherThread){
			//we are being notified that there are incoming transactions for the channel.
			if(keepCapturing){
				//if we're still suppose to update our stuff
				System.out.println("Received an updated transaction amount for a channel and still capturing, updating.");
				channelTransactions.put(((ChannelWatcherThread) arg0).getOIS(), ((TxnMessage) arg1).getFrom()+ "#" + ((TxnMessage) arg1).getAmount());
			}
			else{
				System.out.println("Received an updated transaction amount but stopped capturing, ignoring..");
			}
		}*/
	}
	
	
}
