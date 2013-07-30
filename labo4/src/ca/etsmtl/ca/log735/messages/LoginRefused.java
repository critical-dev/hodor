package ca.etsmtl.ca.log735.messages;

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
		client.loginRefused();
	}
}
