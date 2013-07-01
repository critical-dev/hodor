package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;

import ca.etsmtl.cours.log735.message.TxnMessage;

public class TxnListenerThread extends Thread {
	
	private Branch branch;
	private ObjectInputStream ois;

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
