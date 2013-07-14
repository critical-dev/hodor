package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.model.Room;

/**
 * With this message, the server confirms to the client that a room has been created.
 * Sent in response to a CreateRoomRequest.
 * @author artom
 *
 */
public class CreateRoomResponse extends ClientMessage {

	private Room serverRoom;
	
	public CreateRoomResponse(Room serverRoom){
		this.serverRoom = serverRoom;
	}
	
	@Override
	public void process(Client client) {
		// TODO Auto-generated method stub

	}

}
