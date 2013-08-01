package ca.etsmtl.log735.client;

import java.io.IOException;
import java.io.ObjectInputStream;

import ca.etsmtl.ca.log735.messages.ClientMessage;
/******************************************************
Cours : LOG735
Session : Été 2013
Groupe : 01
Laboratoire : Laboratoire #4
Étudiants : Artom Lifshitz, Chrystophe Chabert
Code(s) perm. : LIFA29108505, CHAC12098902
Date création : 01/07/2013
******************************************************/
public class ClientThread extends Thread {
	
	private Client client;
	private ObjectInputStream ois;
	private volatile boolean run = true;
	
	public ClientThread(Client client, ObjectInputStream ois) {
		this.client = client;
		this.ois = ois;
	}

	@Override
	public void run() {
		while (run) {
			try {
				ClientMessage message = (ClientMessage) ois.readObject();
				message.process(client);
			} catch (IOException e) {
				System.err.println("Server has gone away");
				run = false;
				client.disconnect();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
