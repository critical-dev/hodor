package ca.etsmtl.log735.client;

import java.io.IOException;
import java.io.ObjectInputStream;

import ca.etsmtl.ca.log735.messages.Response;

public class ClientThread extends Thread {
	
	private Client client;
	private ObjectInputStream ois;
	
	public ClientThread(Client client, ObjectInputStream ois) {
		this.client = client;
		this.ois = ois;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Response resp = (Response) ois.readObject();
				resp.process(client);
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
