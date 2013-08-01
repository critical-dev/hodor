package ca.etsmtl.log735.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;

import ca.etsmtl.ca.log735.messages.CreateRoomRequest;
import ca.etsmtl.ca.log735.messages.JoinRoomRequest;
import ca.etsmtl.ca.log735.messages.LoginRequest;
import ca.etsmtl.ca.log735.messages.RegisterRequest;
import ca.etsmtl.ca.log735.messages.SendMessageRequest;
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
	private Queue<Conversation> joinedConvsQueue = new LinkedList<Conversation>();
	private Queue<Room> serverRoomsQueue = new LinkedList<Room>();
	private Queue<Room> roomsWithNewUsers = new LinkedList<Room>();
	
	private void connect(InetAddress serverIp, int port) throws IOException {
		socket = new Socket(serverIp, port);
		ois = new ObjectInputStream(socket.getInputStream());
		clientThread = new ClientThread(this, ois);
		clientThread.start();
		oos = new ObjectOutputStream(socket.getOutputStream());
	}

	public void login(InetAddress serverIp, int port, String username, String password) throws IOException {
		this.serverIp = serverIp;
		this.port = port;
		this.username = username;
		this.password = password;
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
	
	public boolean isConnected() {
		return connected;
	}

	public Conversation nextJoinedConv() {
		return joinedConvsQueue.poll();
	}
	
	public Room nextServerRoom() {
		return serverRoomsQueue.poll();
	}
	
	public Room nextRoomWithNewUsers() {
		return roomsWithNewUsers.poll();
	}

	public void serverRoomsAdd(List<Room> rooms) {
		for (Room room: rooms) {
			serverRoomsQueue.add(room);
		}
		setChanged(); notifyObservers();
	}
	
	public void joinedConvAdd(Conversation convo) {
		joinedConvsQueue.add(convo);
		setChanged(); notifyObservers();
	}

	public void sendJoinRoom(Room room) {
		try {
			oos.writeObject(new JoinRoomRequest(room.getName(), username));
			System.out.println("Sent JoinRoomRequest for " + room.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendCreateRoom(String roomName) {
		try {
			oos.writeObject(new CreateRoomRequest(roomName, username));
			System.out.println("Sent CreateRoomRequest for " + roomName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendMessage(String text, Conversation conv) {
		try {
			oos.writeObject(new SendMessageRequest(text, conv, username));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void refreshUserList(Conversation conversation) {
		roomsWithNewUsers.add((Room) conversation);
		setChanged(); notifyObservers();
	}

	public void disconnect() {
		try {
			oos.close();
			ois.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("Exception while closing streams/socket - this is expected if we're being to disconnect because the server died.");
		}
		connected = false;
		setChanged(); notifyObservers();
	}
}
