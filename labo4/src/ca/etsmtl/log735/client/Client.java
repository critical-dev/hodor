package ca.etsmtl.log735.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;

import ca.etsmtl.ca.log735.messages.LoginRequest;
import ca.etsmtl.ca.log735.messages.RegisterRequest;
import ca.etsmtl.log735.model.Conversation;
import ca.etsmtl.log735.model.Room;
/******************************************************
Cours : LOG735
Session : Été 2013
Groupe : 01
Laboratoire : Laboratoire #4
Étudiants : Artom Lifshitz, Chrystophe Chabert
Code(s) perm. : LIFA29108505, CHAC12098902
Date création : 01/07/2013
******************************************************/
public class Client extends Observable {
	
	private InetAddress serverIp;
	private int port;
	private String username;
	private String password;
	
	private Socket socket = null;
	private ClientThread clientThread = null;
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	
	private boolean connected = false;
	private Queue<Conversation> newConversations = new LinkedList<Conversation>();
	
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
		connected = true;
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

	public Conversation nextConversation() {
		return newConversations.poll();
	}

	public boolean isConnected() {
		return connected;
	}

	public void joinRoom(Room serverRoom) {
		newConversations.add(serverRoom);
	}
}
