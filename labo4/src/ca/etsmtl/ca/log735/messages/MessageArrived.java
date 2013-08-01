package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.model.Conversation;
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
 * Sent by the server to the client to indicate a new message has arrived in a room, group or dialog with a single user.
 * @author artom
 *
 */
public class MessageArrived extends ClientMessage {

	public MessageArrived(String message, Conversation conv) {
		
	}
	
	@Override
	public void process(Client client) {
		// TODO Auto-generated method stub

	}

}
