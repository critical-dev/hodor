package ca.etsmtl.log735.model;

import java.net.InetAddress;
import java.util.Observable;
import java.util.Vector;

public class Client extends Observable {

	private String username;
	private String password;
	private InetAddress serverIp;
	private int serverPort;

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setServerIp(InetAddress ip) {
		this.serverIp = ip;
	}

	public void setServerPort(int port) {
		this.serverPort = port;
	}

	public void sendMessage(Conversation conversation, String text) {
		// TODO Auto-generated method stub
		
	}

	public void start() {
		// TODO Auto-generated method stub
		
	}

	public Vector<Room> getRoomList() {
		// TODO Auto-generated method stub
		Vector<Room> stub = new Vector<Room>();
		stub.add(new Room("Room 1"));
		stub.add(new Room("Room 2"));
		return stub;
		// return null;
	}

	public void joinRoom(String roomName) {
		// TODO Auto-generated method stub
		
	}

	public void createRoom(String roomName, String roomPassword) {
		// TODO Auto-generated method stub
		
	}
}
