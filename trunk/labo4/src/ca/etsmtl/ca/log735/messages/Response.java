package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;

/**
 * A response from the server to the client.
 * @author artom
 *
 */
public abstract class Response {

	/**
	 * Process the message client-side.
	 * @param server
	 */
	public abstract void process(Client client);
}
