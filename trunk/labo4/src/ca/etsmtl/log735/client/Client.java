package ca.etsmtl.log735.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observable;
import java.util.Vector;

import ca.etsmtl.ca.log735.messages.LoginRequest;
import ca.etsmtl.ca.log735.messages.RegisterRequest;

public class Client extends Observable {
	
	private InetAddress serverIp;
	private int port;
	private String username;
	private String password;
	
	private Socket socket = null;
	private ClientThread clientThread = null;
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	
	private State state = State.DISCONNECTED;
	
	public enum State {
		CONNECTED,
		DISCONNECTED,
		LOGIN_REFUSED
	}
	
	public State getState() {
		return state;
	}

	public Vector<String> getRoomList() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasNewConversations() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void connect(InetAddress serverIp, int port) throws IOException {
		if (socket == null && clientThread == null && ois == null && oos == null) {
			socket = new Socket(serverIp, port);
			ois = new ObjectInputStream(socket.getInputStream());
			clientThread = new ClientThread(this, ois);
			clientThread.start();
			oos = new ObjectOutputStream(socket.getOutputStream());
		}
	}

	public void login(InetAddress serverIp, int port, String username, String password) throws IOException {
		connect(serverIp, port);
		oos.writeObject(new LoginRequest(username, password));
	}
	
	public void login() {
		try {
			login(serverIp, port, username, password);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void register(InetAddress serverIp, int port, String username, String password, String passwordConf) throws IOException {
		this.serverIp = serverIp;
		this.port = port;
		this.username = username;
		this.password = password;
		connect(serverIp, port);
		oos.writeObject(new RegisterRequest(username, password, passwordConf));
	}

	public void loginRefused() {
		state = State.LOGIN_REFUSED;
		setChanged(); notifyObservers();
	}
}
