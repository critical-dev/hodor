package ca.etsmtl.ca.log735.messages;

import java.io.Serializable;

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
 * A message from the server to the client.
 * @author artom
 *
 */
public abstract class ClientMessage implements Serializable {
	
	private static final long serialVersionUID = 7593792023631069301L;

	/**
	 * Process the message client-side.
	 * @param client The Client object of the receiving client.
	 */
	public abstract void process(Client client);
}
