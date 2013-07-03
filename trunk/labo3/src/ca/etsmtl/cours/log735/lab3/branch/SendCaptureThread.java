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
	
	public SendCaptureThread(Branch branch) {
		this.branch = branch;		
	}

	@Override
	public void run() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//initial sleep time, just to make sure we have enough clients..
		while (true) {
			//if(System.getProperty("os.name").toLowerCase().contains("windows 7")){
				int delay = (int) (DELAY_MIN + (DELAY_MAX - DELAY_MIN) * Math.random());
				try {
					sleep(delay * 1000);
					//we request a capture
					branch.setRequestingCapture(true);
					System.out.println(branch.getMyId() + " initiating a global capture.");
					for(UUID id : branch.getOutgoingChannelsByUUID().keySet()){						
						ObjectOutputStream oos = branch.getOutgoingChannelsByUUID().get(id);
						//System.out.println("I AM " + branch.getMyId());
						System.out.println("Sending START capture message request to id : " + id);
						oos.writeObject(new StateSyncStartMessage(branch.getMyId()));//request a state capture
						sleep(6000); //sleep again
						System.out.println("Sending STOP capture message request to id : " + id);
						oos.writeObject(new StateSyncStopMessage(branch.getMyId()));//request the previous state capture's response
					}
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
}
