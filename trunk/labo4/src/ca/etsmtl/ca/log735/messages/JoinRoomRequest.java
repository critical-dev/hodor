package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.model.Room;
import ca.etsmtl.log735.server.Server;

/**
 * With this message, the client requests the server to join a room.
 * The client expects a JoinRoomResponse in response.
 * @author artom
 *
 */
public class JoinRoomRequest extends ServerMessage {

	private String roomName;
	private String username;
	private Room room;
	
	//the username is required so it can be added to the room's userlist.
	public JoinRoomRequest(String roomName, String username){
		this.roomName = roomName;
		this.username = username.toLowerCase();
		room = null;
	}
	
	@Override
	public boolean process(Server server) {
		for(int i = 0; i < server.getRooms().size(); i++){
			if(server.getRooms().get(i).getName().equalsIgnoreCase(roomName)){
				if(!server.getRooms().get(i).getUserlist().contains(username)){
					room = server.getRooms().get(i);
					room.getUserlist().add(username);
					return true;
				}
				else{
					System.out.println("JoinRoomRequest : user " + username + " already in that room.");
					return false;
				}
			}
		}
		System.out.println("JoinRoomRequest : Room " + roomName + " does not exist.");
		return false;
	}
	
	public Room getRoom(){
		return room;
	}

}
