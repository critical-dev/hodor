package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.model.Group;
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
 * With this message, the server tells the client that the join group request succeeded or failed.
 * @author chrys, artom
 *
 */
public class JoinGroupResponse extends ClientMessage {

	private Group group;
	
	public JoinGroupResponse(Group group){
		this.group = group;
	}
	
	@Override
	public void process(Client client) {
		// TODO Auto-generated method stub

	}

}
