package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.model.Server;

public abstract class ServerMessage {

	public abstract void process(Server server);
}
