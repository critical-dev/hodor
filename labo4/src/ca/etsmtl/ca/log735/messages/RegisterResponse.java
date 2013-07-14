package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;

/**
 * With his message, the server tells the client that the
 * registration has succeeded if the username is not null.
 * @author chrys
 *
 */
public class RegisterResponse extends ClientMessage {

	private String username;
	
	public RegisterResponse(String username){
		this.username = username;
	}
	
	@Override
	public void process(Client client) {
		// TODO Auto-generated method stub

	}

}
