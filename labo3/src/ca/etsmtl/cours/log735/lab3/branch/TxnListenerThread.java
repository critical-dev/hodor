package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Observable;
import java.util.UUID;

import ca.etsmtl.cours.log735.message.InitialMoneyRequestMessage;
import ca.etsmtl.cours.log735.message.InitialMoneyResponseMessage;
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
	private HashMap<UUID, CaptureStateThread> captureStateThreadsByUUIDs;

	public TxnListenerThread(Branch branch, ObjectInputStream ois) {
		this.branch = branch;
		this.ois = ois;
		captureStateThreadsByUUIDs = new HashMap<UUID, CaptureStateThread>();
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
					//if we receive a state sync message check to see whether
					//we already has a capture state or not (for the requestor), if we did, set the mode accordingly.
					UUID requestorId = ((StateSyncStartMessage) input).getFrom();
					boolean captureStateThreadExistsForRequestor = false;
					for(UUID curRequestorId : captureStateThreadsByUUIDs.keySet()){
						if(requestorId.equals(curRequestorId)){
							captureStateThreadExistsForRequestor = true;
							break;
						}
					}
					if(!captureStateThreadExistsForRequestor){
						//on cree un nouveau thread de capture d'etat qui commence l'enregistrement
						//des sa construction
						captureStateThreadsByUUIDs.put(requestorId, new CaptureStateThread(branch));
					}
					else{
						//on set l'etat du fil de capture a vrai a nouveau
						captureStateThreadsByUUIDs.get(requestorId).setCaptureMode(CaptureStateThread.START_CAPTURE);
					}
				}
				else if (input instanceof StateSyncStopMessage){
					//if we received a request to stop the capture, check to see
					//which capture state thread to stop, depending on the requestor's UUID.
					UUID requestorId = ((StateSyncStartMessage) input).getFrom();
					boolean captureStateThreadExistsForRequestor = false;
					for(UUID curRequestorId : captureStateThreadsByUUIDs.keySet()){
						if(requestorId.equals(curRequestorId)){
							captureStateThreadExistsForRequestor = true;
							break;
						}
					}
					if(captureStateThreadExistsForRequestor){
						//on arrete l'enregistrement et on renvoie au "requestor" notre etat.
						CaptureStateThread captureThreadToStop = captureStateThreadsByUUIDs.get(requestorId);
						captureThreadToStop.setCaptureMode(CaptureStateThread.STOP_CAPTURE);
						//on recupere le texte final du fil de capture et on indique que la branche doit etre
						//mise-a-jour.
						branch.setLastCaptureStateMessage(captureThreadToStop.getCaptureText());
						//on force le gui a afficher le message de capture.
						branch.enforceDisplayCaptureState();
					}
					else{
						//purement pour des fins de debuggage, si on recoit une requete de fin d'enregistrement
						//mais qu'on avait jamais commence, on ne fait rien...
					}
				}
				else if (input instanceof InitialMoneyRequestMessage){
					//get the channel corresponding to the passed in id and reply to it.
					for(UUID id : branch.getOutgoingChannelsByUUID().keySet()){
						if(id.equals(((InitialMoneyRequestMessage) input).getFrom())){
							ObjectOutputStream oos = branch.getOutgoingChannelsByUUID().get(id);
							oos.writeObject(new InitialMoneyResponseMessage(branch.getMyId(), branch.getInitialMoney()));
							System.out.println("Sent initial money amount to " + id + " [" + branch.getInitialMoney() + "]");
							break;
						}
					}
				}
				else if (input instanceof InitialMoneyResponseMessage){
					//if we've received a response to out money request message, add amount to list
					Integer initialMoneyAmtFromBranch = ((InitialMoneyResponseMessage) input).getAmount();
					UUID fromBranchId = ((InitialMoneyResponseMessage) input).getFrom();
					//update our list of initial money amounts.
					branch.getBranchesInitialMoneyAmtList().put(fromBranchId, initialMoneyAmtFromBranch);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
