package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.gui.ClientGUI;
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
 * With this message, the server tells the client that the join group/room request succeeded or failed.
 * @author chrys, artom
 *
 */
public class JoinConversationResponse extends ClientMessage {

	private static final long serialVersionUID = 506021475855849415L;
	private Conversation convo;
	
	public JoinConversationResponse(Conversation convo) {
		this.convo = convo;
	}
	
	@Override
	public void process(Client client) {
		if (convo != null) {
			client.joinedConvAdd(convo);
			System.out.println("Processed JoinedConversationResponse for " + convo.getName());
		} else {
			ClientGUI.error("Join refused");
		}
	}

}
