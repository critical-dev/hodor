package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.server.Server;

/**
 * With this message, the client asks the server to login.
 * Adds the user to the server's authenticated users list
 * and returns true if the user was successfully authenticated.
 * @author chrys, artom
 *
 */
public class LoginRequest extends ServerMessage {
	
	private String username, password;
	
	public LoginRequest(String username, String password) {
		this.username = username.toLowerCase();
		this.password = password;
	}

	@Override
	public boolean process(Server server) {
		if(server.authenticateUser(username, password)){
			server.getAuthenticatedUsers().add(username);
			System.out.println("LoginRequest : Adding " + username + " to authenticated users list.");
			return true;
		}
		else{
			return false;
		}
	}
	
	public String getUsername(){
		return username;
	}
}
