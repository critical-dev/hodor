package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.model.Room;
import ca.etsmtl.log735.server.Server;
/******************************************************
Cours : LOG735
Session : Été 2013
Groupe : 01
Laboratoire : Laboratoire #4
Étudiants : Artom Lifshitz, Chrystophe Chabert
Code(s) perm. : LIFA29108505, CHAC12098902
Date création : 01/07/2013
******************************************************/
/**
 * With this message, the clients requests the creation of a room.
 * Expects a CreateRoomResponse in response.
 * @author chrys
 *
 */
public class CreateRoomRequest extends ServerMessage {

	private String roomName;
	private Room createdRoom;
	private String username;//the username who initiated the request.
	private String roomPassword;
	
	//crée une salle sans mot de passe
	public CreateRoomRequest(String roomName, String username){
		this.roomName = roomName;
		this.username = username;
		createdRoom = null;
		roomPassword = null;
	}
	
	public CreateRoomRequest(String roomName, String username, String roomPassword){
		this.roomName = roomName;
		this.username = username;
		createdRoom = null;
		this.roomPassword = roomPassword;
	}
	
	@Override
	public boolean process(Server server) {
		for(int i =0 ; i < server.getRooms().size(); i++){
			if(roomName == null || server.getRooms().get(i).getName().equalsIgnoreCase(roomName.trim())){
				System.out.println("CreateRoomRequest : Room " + roomName + " already exists. Aborting.");
				return false;
			}
		}
		if(server.getAuthenticatedUsers().contains(username)){
			if(roomPassword == null || roomPassword.trim().isEmpty())
				createdRoom = new Room(roomName);
			else
				createdRoom = new Room(roomName, roomPassword);
			createdRoom.getUserlist().add(username);
			server.getRooms().add(createdRoom);
			System.out.println("CreateRoomRequest : Room " + roomName + " created with [" + createdRoom.getUserlist().size() + " user], password? " + (createdRoom.isPasswordProtected()?"yes":"no"));
			return true;
		}
		else{
			System.out.println("CreateRoomRequest : User " + username + " not authenticated, aborting.");
			return false;
		}
		
	}
	
	public Room getRoom(){
		return createdRoom;
	}
	
	public String getRoomCreateRequester(){
		return username;
	}

}
