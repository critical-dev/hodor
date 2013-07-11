package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.server.Server;

/**
 * A message from the client to the server.
 * @author artom
 *
 */
public abstract class ServerMessage {

	/**
	 * Process the message server-side.
	 * @param server The server object of the receiving server.
	 */
	public abstract void process(Server server);
}
