package ca.etsmtl.log735.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import ca.etsmtl.log735.model.Group;
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
public class Server {
	
	public static int SERVER_CLIENT_LISTEN_PORT = 1142;
	//username outputstream list
	private HashMap<String, ObjectOutputStream> clientOutputStreams;
	//username input stream list
	private HashMap<String, ObjectInputStream> clientInputStreams;
	//known users list with passwords
	private HashMap<String, String> usersWithPasswords;
	//authenticated IPs
	private ArrayList<String> authenticatedUsers;
	private HashMap<InetAddress, String> authenticatedIps;
	//chat room msgs
	private ArrayList<Room> rooms;
	//group msgs
	private HashMap<Group, ArrayList<String>> groupsWithConversations;
	private Room defaultRoom;
	
	private ServerSocket serverSocket;
	private File authList;
	private static String authListDirLoccation = "data";
	private static String authListFileRelativeLocation = authListDirLoccation + File.separator + "authList.txt";
	
	public static void main(String[] args) {
		new Server();
	}
	
	public Server(){
		
		// init stuff
		clientOutputStreams = new HashMap<String, ObjectOutputStream>();
		clientInputStreams = new HashMap<String, ObjectInputStream>();
		usersWithPasswords = new HashMap<String, String>();
		authenticatedUsers = new ArrayList<String>();
		authenticatedIps = new HashMap<InetAddress, String>();
		rooms = new ArrayList<Room>();
		groupsWithConversations = new HashMap<Group, ArrayList<String>>();
		
		
		//create default room
		defaultRoom = new Room("DefaultRoom");
				
		authList = new File(authListFileRelativeLocation);
		if(authList == null || !authList.exists()){
			System.out.println("Database file : " + authList.getAbsolutePath() + " does not exist. Creating empty DB file.");
			try {
				new File(authListDirLoccation).mkdirs();
				authList.createNewFile();//create empty file if it does not exist.
				FileWriter fw = new FileWriter(authList);
				fw.write("Auth Users List");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//set known users list
		try {
			FileReader fr = new FileReader(authList);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			System.out.println("Loading DB file.");
			while((line = br.readLine()) != null){
				if(line.indexOf(":") != -1){
					String[] splitString = line.split(":");
					String username = splitString[0];
					String password = splitString[1];
					System.out.println("Read user : " + username + " with pass: " + password);
					if(password.length() > 4)
						usersWithPasswords.put(username, password);
				}
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Loaded : " + usersWithPasswords.size() + " valid users from database.");
		try {
			serverSocket = new ServerSocket(SERVER_CLIENT_LISTEN_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		init();
	}
	
	private void init(){
		System.out.println("Server now listening for client connexions.");
		while(true){
			try {
				Socket incomingClient = serverSocket.accept();
				new ServerThread(this, incomingClient).start();
				System.out.println("***********************");
				System.out.println("SERVER STATUS : ");
				System.out.println("Nb authenticated users : " + authenticatedUsers.size());
				for(int i = 0; i < authenticatedUsers.size(); i++){
					System.out.println(authenticatedUsers.get(i));
				}
				System.out.println("-----------------------");
				System.out.println("Nb rooms : " + rooms.size());
				for(int i = 0; i < rooms.size(); i++){
					System.out.println(rooms.get(i).getName() + " [NB USERS:" + rooms.get(i).getUserlist().size() + "]");
				}
				System.out.println("***********************");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	

	//authbenticates a user against the server's users list
	public boolean authenticateUser(String username, String password){
		for(String user : usersWithPasswords.keySet()){
			if(user.equalsIgnoreCase(username)){
				if(usersWithPasswords.get(user).equals(password)){
					System.out.println("Server : User : " + username + " authenticated.");
					return true;
				}
				else{
					System.out.println("Server : USer : " + username + " incorrectly authenticated.");
					return false;
				}
			}
		}
		System.out.println("Server : User : " + username + " not registered in the database.");
		return false;
	}
	
	//structural changes to a HashMap should be synchronized
	public synchronized void addClientOutputStream(String username, ObjectOutputStream clientOutputStream){
		clientOutputStreams.put(username, clientOutputStream);
	}

	public HashMap<String, ObjectOutputStream> getClientsOutputStreams() {
		return clientOutputStreams;
	}

	public HashMap<String, ObjectInputStream> getClientInputStreams() {
		return clientInputStreams;
	}

	public HashMap<String, String> getUsersWithPasswords() {
		return usersWithPasswords;
	}

	public void setUsersWithPasswords(HashMap<String, String> usersWithPasswords) {
		this.usersWithPasswords = usersWithPasswords;
	}

	public ArrayList<String> getAuthenticatedUsers() {
		return authenticatedUsers;
	}

	public File getAuthList() {
		return authList;
	}

	public void setAuthList(File authList) {
		this.authList = authList;
	}

	public Room getDefaultRoom() {
		synchronized (defaultRoom) {
			return defaultRoom;
		}
	}

	public void setDefaultRoom(Room defaultRoom) {
		synchronized (defaultRoom) {
			this.defaultRoom = defaultRoom;
		}
	}

	public ArrayList<Room> getRooms() {
		synchronized (rooms) {
			return rooms;
		}
	}

	public void setRoomsWithConversations(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}

	public HashMap<Group, ArrayList<String>> getGroupsWithConversations() {
		return groupsWithConversations;
	}

	public void setGroupsWithConversations(HashMap<Group, ArrayList<String>> groupsWithConversations) {
		this.groupsWithConversations = groupsWithConversations;
	}

	public HashMap<InetAddress, String> getAuthenticatedIps() {
		return authenticatedIps;
	}

	public void setAuthenticatedIps(HashMap<InetAddress, String> authenticatedIps) {
		this.authenticatedIps = authenticatedIps;
	}
	
}
