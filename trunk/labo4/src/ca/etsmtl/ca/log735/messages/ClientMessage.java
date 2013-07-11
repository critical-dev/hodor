package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;

/**
 * A message from the server to the client.
 * @author artom
 *
 */
public abstract class ClientMessage {
	
	/**
	 * Process the message client-side.
	 * @param client The Client object of the receiving client.
	 */
	public abstract void process(Client client);
}
