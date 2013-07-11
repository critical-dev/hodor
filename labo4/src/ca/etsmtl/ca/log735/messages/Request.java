package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.server.Server;

/**
 * A request from the client to the server.
 * @author artom
 *
 */
public abstract class Request {
	
	/**
	 * Process the message server-side.
	 * @param client
	 */
	public abstract void process(Server server);
}
