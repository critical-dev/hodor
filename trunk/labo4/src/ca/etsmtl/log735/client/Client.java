package ca.etsmtl.log735.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observable;
import java.util.Vector;

import ca.etsmtl.ca.log735.messages.LoginRequest;
import ca.etsmtl.ca.log735.messages.RegisterRequest;

public class Client extends Observable {
	
	private boolean connected = false;
	private Socket socket = null;
	private ClientThread clientThread = null;
	private ObjectOutputStream oos = null;

	public Vector<String> getRoomList() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isConnected() {
		return connected;
	}

	public boolean hasNewConversations() {
		// TODO Auto-generated method stub
		return false;
	}

	public void login(InetAddress serverIp, int port, String username, String password) throws IOException {
		socket = new Socket(serverIp, port);
		clientThread = new ClientThread(this, socket);
		clientThread.start();
		oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(new LoginRequest(username, password));
	}

	public void register(InetAddress serverIp, int port, String username, String password, String passwordConf) throws IOException {
		socket = new Socket(serverIp, port);
		clientThread = new ClientThread(this, socket);
		clientThread.start();
		oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(new RegisterRequest(username, password, passwordConf));
	}

	public void loginRefused() throws IOException {
		clientThread.disconnect();
		oos.close();
		socket.close();
	}
}
