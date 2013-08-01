package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.model.Room;
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
 * With this message, the server tells the client to join a room.
 * Can be sent in response to a LoginRequest (to join the default room)
 * or in response to a JoinRoomRequest (to confirm joining the request room).
 * In either case, the provided server room is set to null if the join request failed
 * for any reason, otherwise it returns the room requested in a previous request.
 * @author chrys, artom
 *
 */
public class JoinRoomResponse extends ClientMessage {

	private static final long serialVersionUID = 1274786739208726564L;
	
	private Room serverRoom;
	
	public JoinRoomResponse(Room serverRoom){
		this.serverRoom = serverRoom;
	}
	
	@Override
	public void process(Client client) {
		client.joinRoom(serverRoom);
	}

}
