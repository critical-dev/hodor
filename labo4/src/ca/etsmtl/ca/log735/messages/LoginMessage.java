package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.server.Server;

public class LoginMessage extends ServerMessage {
	
	private String username, password;
	
	public LoginMessage(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public void process(Server server) {
		// TODO Auto-generated method stub

	}

}
