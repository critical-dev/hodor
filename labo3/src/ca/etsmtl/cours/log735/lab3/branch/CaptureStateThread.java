package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.UUID;

import ca.etsmtl.cours.log735.lab3.bank.Bank;
import ca.etsmtl.cours.log735.message.MoneyAmountRequestMessage;
import ca.etsmtl.cours.log735.message.TotalMoneyRequestMessage;
import ca.etsmtl.cours.log735.message.TxnMessage;

/**
 * Cette classe notifie la branche des qu'une capture est terminee.
 * 
 * */
public class CaptureStateThread extends Observable{
	
	private volatile String tempChannelsText;
	private volatile int totalCaptureMoneyAmount;
	private volatile int tempCaptureMoneyAmt;
	private volatile Branch branch;
	private volatile boolean keepCapturing;
	private CaptureRunner captureRunner;
	
	public static final boolean START_CAPTURE = true, STOP_CAPTURE = false;
	
	public CaptureStateThread(Branch branch){
		this.branch = branch;
		keepCapturing = true;
		totalCaptureMoneyAmount = 0;
		tempCaptureMoneyAmt = 0;
		captureRunner = new CaptureRunner();
		captureRunner.start();
	}
	
	/*
	 * Cette classe interne se charge de faire l'ecoute des canaux et de capturer
	 * les transactions. 
	 * **/
	class CaptureRunner extends Thread{

		private volatile boolean isAlreadyCapturing;
		private String captureText = "";
		
		public CaptureRunner(){
			isAlreadyCapturing = false;
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
							System.out.print("Attempting to request initial money amount to branch.. " + id);
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
					new GetBankTotalAmountThread().start();
				}
				else{
					//une fois les etats initiaux enregistres, on enregistre les canaux
					//mais on enregistre dans une variable temporaire, le temps de recevoir
					//le message de fin d'ecoute.
					tempChannelsText = "";
					tempCaptureMoneyAmt = 0;
					for(UUID curBranchId : branch.getIncomingChannelsByUUID().keySet()){
						ObjectInputStream tmpOIS = branch.getIncomingChannelsByUUID().get(curBranchId);
						try {
							Object input = tmpOIS.readObject();
							if(input instanceof TxnMessage){
								//we captured a transaction this time.
								tempChannelsText += "Canal S" + branch.getMyId() + " - S" + curBranchId + ": " + ((TxnMessage) input).getAmount()+ "$\n";
								tempCaptureMoneyAmt += ((TxnMessage) input).getAmount();
							}
						} catch (ClassNotFoundException e) {
							System.err.println(">> Expected error : " + e.getMessage());
						} catch (IOException e) {
							System.err.println(">> Expected error : " + e.getMessage());
						}
					}
					while(Branch.BANK_LAST_KNOWN_TOTAL_AMOUNT == 0){
						//if we haven't updated yet, wait a bit..
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					tempChannelsText += "Somme connue par la banque : " + Branch.BANK_LAST_KNOWN_TOTAL_AMOUNT + "$\n";
					tempChannelsText += "Somme detectee par la capture : " + (totalCaptureMoneyAmount + tempCaptureMoneyAmt) + "$\n";
					tempChannelsText += "ETAT GLOBAL " + (Bank.BANK_TOTAL_MONEY_IN_THE_SYSTEM == (totalCaptureMoneyAmount + tempCaptureMoneyAmt) ? "COHERENT":"INCOHERENT (delta :" + (Bank.BANK_TOTAL_MONEY_IN_THE_SYSTEM - (totalCaptureMoneyAmount + tempCaptureMoneyAmt)) + ")") + "\n";
				}
			}//fin while keepCapturing
			//une fois la capture terminee, on assemble le tout ensemble.
			captureText += tempChannelsText;
			System.out.println("Final capture for this branch : ");
			System.out.println(captureText);
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
			captureRunner = new CaptureRunner();
			captureRunner.start();
		}
		else{
			try {
				//force a 2 secs sleep to let the capture runner stop.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public String getCaptureText() {
		return captureRunner.getCaptureText();
	}
	
	class GetBankTotalAmountThread extends Thread{
		public GetBankTotalAmountThread(){};
		@Override
		public void run(){
			try {
				Socket client = new Socket(branch.getBankIp(), Bank.PORT);
				ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
				System.out.println("Sent request to get bank total money amount.");
				oos.writeObject(new TotalMoneyRequestMessage(branch.getMyId()));
				oos.close();
				client.close();
			} catch (IOException e) {
				System.err.println(">> Error occured when requesting total money amount to bank.");
				e.printStackTrace();
			}
		}
	}
}
