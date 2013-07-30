package ca.etsmtl.log735.client;

import java.io.IOException;
import java.io.ObjectInputStream;

import ca.etsmtl.ca.log735.messages.ClientMessage;

public class ClientThread extends Thread {
	
	private Client client;
	private ObjectInputStream ois;
	
	public ClientThread(Client client, ObjectInputStream ois) {
		this.client = client;
		this.ois = ois;
		System.out.println("clientthread constructor done");
	}

	@Override
	public void run() {
		while (true) {
			try {
				ClientMessage message = (ClientMessage) ois.readObject();
				message.process(client);
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
