package ca.etsmtl.ca.log735.messages;

import java.io.Serializable;

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
 * A message from the client to the server.
 * @author artom
 *
 */
public abstract class ServerMessage implements Serializable {

	private static final long serialVersionUID = 6553439491230586430L;

	/**
	 * Process the message server-side.
	 * @param server The server object of the receiving server.
	 */
	public abstract boolean process(Server server);
}
