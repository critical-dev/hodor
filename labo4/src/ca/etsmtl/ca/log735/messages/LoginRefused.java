package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;
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
