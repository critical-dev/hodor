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
	
	public CreateRoomRequest(String roomName){
		this.roomName = roomName;
		createdRoom = null;
	}
	
	@Override
	public boolean process(Server server) {
		if(!server.getRooms().contains(roomName)){
			createdRoom = new Room(roomName);
			server.getRooms().add(createdRoom);
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
