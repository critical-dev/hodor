package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.gui.ClientGUI;
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
 * With his message, the server tells the client that the
 * registration has succeeded if the username is not null.
 * @author chrys
 *
 */
public class RegisterResponse extends ClientMessage {

	private static final long serialVersionUID = 8067584295435986724L;
	
	private String username;
	
	public RegisterResponse(String username){
		this.username = username;
	}
	
	@Override
	public void process(Client client) {
		if (username != null) {
			client.login();
		} else {
			ClientGUI.error("Registration refused");
		}
	}
}
