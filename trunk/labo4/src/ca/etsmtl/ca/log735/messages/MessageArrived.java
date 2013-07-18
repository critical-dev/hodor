package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;

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
