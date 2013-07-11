package ca.etsmtl.log735.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observable;
import java.util.Vector;

import ca.etsmtl.ca.log735.messages.LoginMessage;
import ca.etsmtl.log735.model.Group;
import ca.etsmtl.log735.model.Room;

public class Client extends Observable {

	private String username, password;
	private InetAddress serverIp;
	private int serverPort;
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

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

	public void sendMessage(Group group, String text) {
		// TODO Auto-generated method stub
		
	}

	public void start() {
		try {
			Socket socket = new Socket(serverIp, serverPort);
			ois = new ObjectInputStream(socket.getInputStream());
			new ClientThread(this, ois).start();
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(new LoginMessage(username, password));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Vector<Room> getRoomList() {
		// TODO Auto-generated method stub
		return null;
	}

	public void joinRoom(String roomName) {
		// TODO Auto-generated method stub
		
	}

	public void createRoom(String roomName, String roomPassword) {
		// TODO Auto-generated method stub
		
	}
}
