package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.server.Server;
/******************************************************
Cours : LOG735
Session : Été 2013
Groupe : 01
Laboratoire : Laboratoire #4
Étudiants : Artom Lifshitz, Chrystophe Chabert
Code(s) perm. : LIFA29108505, CHAC12098902
Date création : 01/07/2013
******************************************************/
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
		if(!server.getAuthenticatedUsers().contains(username) && server.authenticateUser(username, password)){
			server.getAuthenticatedUsers().add(username);
			System.out.println("LoginRequest : Adding " + username + " to authenticated users list.");
			return true;
		}
		else{
			System.out.println("LoginRequest : Authentication error, bad credentials or user already logged on from other machine.");
			return false;
		}
	}
	
	public String getUsername(){
		return username;
	}
}
