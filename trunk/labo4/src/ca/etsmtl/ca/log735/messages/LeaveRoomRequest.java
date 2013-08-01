package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.model.Group;
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
 * Removes a user from a room
 * @author chrys
 *
 */
public class LeaveRoomRequest extends ServerMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2521285633403217157L;
	private String roomName;
	private String username;
	
	public LeaveRoomRequest(String roomName, String username){
		this.roomName = roomName;
		this.username = username.toLowerCase();
	}
	
	@Override
	public boolean process(Server server) {
		int indexOfRoom = server.getRooms().indexOf(roomName);
		if(indexOfRoom != -1){
			if(server.getRooms().get(indexOfRoom).getUserlist().contains(username)){
				server.getRooms().get(indexOfRoom).getUserlist().remove(username);
				System.out.println("LeaveRoomRequest: Successfully removed user "+username+" from room "+roomName);
				//if no more users in that room we delete it, except for the default room.
				if(server.getRooms().get(indexOfRoom) != server.getDefaultRoom() && server.getRooms().get(indexOfRoom).getUserlist().size() == 0){
					System.out.println("LeaveRoomRequest: No more users in that room, removed it. ");
					server.getRooms().remove(indexOfRoom);
				}
			}
			else{
				System.out.println("LeaveRoomRequest: user "+username+" not in that room. No effect.");
			}
		}
		else{
			System.err.println("LeaveRoomRequest: can't remove user from room "+roomName+", room doesn't exist.");
			return false;
		}
		return true;
	}
}
