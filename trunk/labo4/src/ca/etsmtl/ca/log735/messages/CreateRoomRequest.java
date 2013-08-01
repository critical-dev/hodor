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
	public CreateRoomRequest(String roomName, String username){
		this.roomName = roomName;
		this.username = username;
		createdRoom = null;
	}
	
	@Override
	public boolean process(Server server) {
		if(!server.getRooms().contains(roomName)){
			int userIndex = server.getAuthenticatedUsers().indexOf(username);
			String user = null;
			if(userIndex != -1){
				user = server.getAuthenticatedUsers().get(userIndex);
			}
			else{
				return false;
			}
			createdRoom = new Room(roomName);
			createdRoom.getUserlist().add(user);
			server.getRooms().add(createdRoom);
			System.out.println("CreateRoomRequest : Room " + roomName + " created with [" + createdRoom.getUserlist().size() + " user].");
			return true;
		}
		else{
			System.out.println("CreateRoomRequest : Room " + roomName + " already exists.");
			return false;
		}
	}
	
	public Room getRoom(){
		return createdRoom;
	}

}
