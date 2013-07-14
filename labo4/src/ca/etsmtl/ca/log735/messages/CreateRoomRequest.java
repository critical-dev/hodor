package ca.etsmtl.ca.log735.messages;

import java.util.ArrayList;

import ca.etsmtl.log735.model.Room;
import ca.etsmtl.log735.server.Server;

/**
 * With this message, the clients requests the creation of a room.
 * Expects a CreateRoomResponse in response.
 * @author artom
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
