package ca.etsmtl.log735.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import ca.etsmtl.ca.log735.messages.ClientMessage;

public class ClientThread extends Thread {
	
	private Client client;
	private ObjectInputStream ois;
	private boolean run = true;
	
	public ClientThread(Client client, Socket socket) throws IOException {
		this.client = client;
		this.ois = new ObjectInputStream(socket.getInputStream());
	}

	@Override
	public void run() {
		while (run) {
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

	public void disconnect() throws IOException {
		run = false;
		while (isAlive());
		ois.close();
	}
}
