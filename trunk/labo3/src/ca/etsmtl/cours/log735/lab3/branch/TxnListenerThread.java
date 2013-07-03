package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Observable;
import java.util.UUID;

import ca.etsmtl.cours.log735.message.InitialMoneyRequestMessage;
import ca.etsmtl.cours.log735.message.InitialMoneyResponseMessage;
import ca.etsmtl.cours.log735.message.MoneyAmountRequestMessage;
import ca.etsmtl.cours.log735.message.MoneyAmountResponseMessage;
import ca.etsmtl.cours.log735.message.StateMessage;
import ca.etsmtl.cours.log735.message.StateSyncStartMessage;
import ca.etsmtl.cours.log735.message.StateSyncStopMessage;
import ca.etsmtl.cours.log735.message.TxnMessage;

public class TxnListenerThread extends Thread {
	
	private Branch branch;
	private ObjectInputStream ois;
	/**
	 * On tient une liste de fils d'execution de capture d'etats
	 * par succursale qui le requiert. De cette maniere, on rencontre les exigences:
	 * 
	 * ETAT-03 : Le système bancaire doit supporter plusieurs captures de l’état global simultanées et 
	   asynchrones. En d’autres termes, le fait qu’une ou plusieurs captures soient présentement en cours à 
	   travers le système ne doit pas influencer le fonctionnement d’une nouvelle capture.
		
	   ETAT-04 : Une Succursale doit être en mesure de fonctionner sans interruption ni changements pendant 
	   qu’une capture de l’état global est en cours à partir de celle-ci. Le processus doit être transparent et ne 
	   doit pas affecter significativement la performance du système.
	 * */

	public TxnListenerThread(Branch branch, ObjectInputStream ois) {
		this.branch = branch;
		this.ois = ois;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Object input = ois.readObject();
				if (input instanceof TxnMessage) {
					branch.recvMoney(((TxnMessage) input).getFrom(), ((TxnMessage) input).getAmount());
				}
				else if (input instanceof StateSyncStartMessage){
					//if we receive a state sync start message, add the requestor to the list
					//of requestor
					UUID requestorId = ((StateSyncStartMessage) input).getFrom();
					//only one request per branch supported for now
					if(!branch.getCapStateRequestors().contains(requestorId) && !requestorId.equals(branch.getMyId())){
						branch.getCapStateRequestors().add(requestorId);//add the requestor
						branch.getMyCaptureStateThread().setCaptureMode(CaptureStateThread.START_CAPTURE);
					}
				}
				else if (input instanceof StateSyncStopMessage){
					//if we received a request to stop the capture, stop the capture
					//and return to the requestor
					UUID requestorId = ((StateSyncStopMessage) input).getFrom();
					branch.getMyCaptureStateThread().setCaptureMode(CaptureStateThread.STOP_CAPTURE);
					System.out.println("Received Stop Capture Message, stopping capture.");
					if(branch.getCapStateRequestors().remove(requestorId)) System.out.println("Removed " + requestorId + " from requestors list.");
					else System.out.println("Failed to remove " + requestorId + " from requestors list.");
					ObjectOutputStream oos = branch.getOutgoingChannelsByUUID().get(requestorId);
					oos.writeObject(new StateMessage(branch.getMyId(), branch.getLastCaptureStateMessage()));
					System.out.println("Sent StateMessage to requestor " + requestorId);
				}
				else if (input instanceof StateMessage){
					UUID requestorId = ((StateMessage) input).getFrom();
					branch.setNbStateAnswersReceived(branch.getNbStateAnswersReceived() + 1);
					branch.mergeCaptureMessageInfo(((StateMessage) input).getOutput());
					if(branch.getNbStateAnswersReceived() == branch.getPeerIds().size()){
						branch.enforceDisplayCaptureState();//show the global state if all answers were received.
					}
					else{
						System.out.println("Branch, received state from : " + requestorId + ", awaiting other states..");
					}
				}
				else if (input instanceof InitialMoneyRequestMessage){
					//get the channel corresponding to the passed in id and reply to it.
					System.out.println("Got an initial money request message ..");
					UUID requestorId = ((InitialMoneyRequestMessage) input).getFrom();
					ObjectOutputStream oos = branch.getOutgoingChannelsByUUID().get(requestorId);
					oos.writeObject(new InitialMoneyResponseMessage(branch.getMyId(), branch.getInitialMoney()));
					System.out.println("Sent INITIAL money amount to requestor " + requestorId + " [" + branch.getInitialMoney() + "]");
					
				}
				else if (input instanceof InitialMoneyResponseMessage){
					System.out.println("Received response to INITIAL money request message .. updating total bank amount");
					//if we've received a response to out money request message, add amount to list
					Integer moneyAmtFromBranch = ((InitialMoneyResponseMessage) input).getAmount();
					UUID requestorId = ((InitialMoneyResponseMessage) input).getFrom();
					//update our list of initial money amounts.
					branch.setBankLastKnownTotalMoneyAmount(branch.getBankLastKnownTotalMoneyAmount() + moneyAmtFromBranch);
				}
				else if (input instanceof MoneyAmountRequestMessage){
					//get the channel corresponding to the passed in id and reply to it.
					System.out.println("Got an initial money request message ..");
					UUID requestorId = ((MoneyAmountRequestMessage) input).getFrom();
					ObjectOutputStream oos = branch.getOutgoingChannelsByUUID().get(requestorId);
					oos.writeObject(new MoneyAmountResponseMessage(branch.getMyId(), branch.getCurrentMoney()));
					System.out.println("Sent money amount to requestor " + requestorId + " [" + branch.getCurrentMoney() + "]");
					
				}
				else if (input instanceof MoneyAmountResponseMessage){
					System.out.println("Received response to money request message .. updating that branch's money amount");
					//if we've received a response to out money request message, add amount to list
					Integer moneyAmtFromBranch = ((MoneyAmountResponseMessage) input).getAmount();
					UUID requestorId = ((MoneyAmountResponseMessage) input).getFrom();
					//update our list of initial money amounts.
					branch.getBranchesMoneyAmtList().put(requestorId, moneyAmtFromBranch);
				}
			} catch (IOException e) {
				if(e instanceof java.io.OptionalDataException){
					System.err.println("OptionalDataException occurred .. " + e.getLocalizedMessage());
				}
				else if(e instanceof java.net.SocketException){
					System.err.println("Java Socket exception occured, aborting..");break;
				}			
				else{
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
