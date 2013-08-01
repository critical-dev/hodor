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
 * With this message, the client requests the server to join a room.
 * The client expects a JoinRoomResponse in response.
 * @author artom
 *
 */
public class JoinRoomRequest extends ServerMessage {

	private String roomName;
	private String username;
	private Room room;
	private String password;
	
	//the username is required so it can be added to the room's userlist.
	public JoinRoomRequest(String roomName, String username){
		this.roomName = roomName;
		this.username = username.toLowerCase();
		room = null;
		password = null;
	}
	
	//the username is required so it can be added to the room's userlist.
	public JoinRoomRequest(String roomName, String username, String password){
		this.roomName = roomName;
		this.username = username.toLowerCase();
		room = null;
		this.password = password;
	}
		
	@Override
	public boolean process(Server server) {
		for(int i = 0; i < server.getRooms().size(); i++){
			if(server.getRooms().get(i).getName().equalsIgnoreCase(roomName)){
				if(!server.getRooms().get(i).getUserlist().contains(username)){
					room = server.getRooms().get(i);
					if(room.isPasswordProtected()){
						if(password != null && password.equals(room.getRoomPassword())){
							System.out.println("JoinRoomRequest : Password match, user added to room.");
							room.getUserlist().add(username);
						}
						else{
							System.err.println("JoinRoomRequest : user's password does not match, or no password provided, aborting request.");
							return false;
						}
					}
					else{
						System.out.println("JoinRoomRequest : Added user to room.");
						room.getUserlist().add(username);
					}
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
