package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.model.Group;

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
