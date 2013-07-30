package ca.etsmtl.ca.log735.messages;

import java.io.IOException;

import ca.etsmtl.log735.client.Client;

/**
 * With his message, the server tells the client that login has been refused.
 * @author artom
 *
 */
public class LoginRefused extends ClientMessage {

	private static final long serialVersionUID = -3091621356771397016L;

	@Override
	public void process(Client client) {
		try {
			client.loginRefused();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
