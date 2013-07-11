package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.server.Server;

/**
 * A login request.
 * @author artom
 *
 */
public class Login extends Request {
	
	private String username, password;
	
	public Login(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public void process(Server server) {
		// TODO Auto-generated method stub

	}

}
