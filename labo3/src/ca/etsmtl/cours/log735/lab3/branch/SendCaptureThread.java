package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ca.etsmtl.cours.log735.message.StateSyncStartMessage;
import ca.etsmtl.cours.log735.message.StateSyncStopMessage;


public class SendCaptureThread extends Thread {
	
	private final static int DELAY_MIN = 10;
	private final static int DELAY_MAX = 30;
	
	private Branch branch;
	private List<UUID> peerIDs = new ArrayList<UUID>();
	
	public SendCaptureThread(Branch branch) {
		this.branch = branch;
		peerIDs.addAll(branch.getOutgoingChannelsByUUID().keySet());
	}

	@Override
	public void run() {
		//while (true) {
			int delay = (int) (DELAY_MIN + (DELAY_MAX - DELAY_MIN) * Math.random());
			try {
				sleep(delay * 1000);
				UUID id = peerIDs.get((int) (peerIDs.size() * Math.random()));
				ObjectOutputStream oos = branch.getOutgoingChannelsByUUID().get(id);
				oos.writeObject(new StateSyncStartMessage(branch.getMyId()));//request a state capture
				sleep(delay / 2); //sleep for about half the time before sending the response request.
				oos.writeObject(new StateSyncStopMessage(branch.getMyId()));//request the previous state capture's response
			} catch (InterruptedException e) {
				System.err.println(">> Error occured in sendCaptureThread !");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println(">> Error occured in sendCaptureThread !");
				e.printStackTrace();
			}
		//}
	}
}
