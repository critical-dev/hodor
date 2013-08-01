package ca.etsmtl.ca.log735.messages;

import java.util.ArrayList;
import java.util.List;

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
 * With this message, the server tells the client all the exisiting rooms.
 * @author chrys, artom
 *
 */
public class RoomListResponse extends ClientMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3917870958088407622L;
	private ArrayList<Room> serverRooms;
	
	public RoomListResponse(ArrayList<Room> serverRooms){
		this.serverRooms = serverRooms;
	}
	
	@Override
	public void process(Client client) {
		// TODO Auto-generated method stub

	}

}
