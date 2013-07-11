package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;

/**
 * With this message, the server tells the client to join a room.
 * Can be sent in response to a LoginRequest (to join the default room)
 * or in response to a JoinRoomRequest (to confirm joining the request room).
 * @author artom
 *
 */
public class JoinRoomResponse extends ClientMessage {

	@Override
	public void process(Client client) {
		// TODO Auto-generated method stub

	}

}
