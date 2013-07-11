package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.model.Client;

public abstract class ClientMessage {
	
	public abstract void process(Client client);
}
