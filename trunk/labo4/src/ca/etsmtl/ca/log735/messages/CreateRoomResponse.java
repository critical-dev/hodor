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
 * With this message, the server confirms to the client that a room has been created.
 * Sent in response to a CreateRoomRequest.
 * @author chrys
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
