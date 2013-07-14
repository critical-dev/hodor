package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.model.Room;

/**
 * With this message, the server tells the client to join a room.
 * Can be sent in response to a LoginRequest (to join the default room)
 * or in response to a JoinRoomRequest (to confirm joining the request room).
 * In either case, the provided server room is set to null if the join request failed
 * for any reason, otherwise it returns the room requested in a preivous request.
 * @author chrys, artom
 *
 */
public class JoinRoomResponse extends ClientMessage {

	private Room serverRoom;
	
	public JoinRoomResponse(Room serverRoom){
		this.serverRoom = serverRoom;
	}
	
	@Override
	public void process(Client client) {
		// TODO Auto-generated method stub

	}

}
