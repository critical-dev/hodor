package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.model.Conversation;
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
 * With this message, the server tells the client that this conversation's users lsit has been updated.
 * @author chrys, artom
 *
 */
public class RefreshUserListResponse extends ClientMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3917870958088407622L;
	private Conversation conversation;
	
	public RefreshUserListResponse(Conversation conversation){
		this.conversation = conversation;
	}
	
	@Override
	public void process(Client client) {
		client.refreshUserList(conversation);
	}
}
