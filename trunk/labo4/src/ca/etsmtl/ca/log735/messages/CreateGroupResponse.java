package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.model.Group;

/**
 * With this message, the server confirms to the client that a group has been created.
 * Sent in response to a CreateGroupRequest.
 * @author chrys
 *
 */
public class CreateGroupResponse extends ClientMessage {

	private Group group;
	
	public CreateGroupResponse(Group group){
		this.group = group;
	}
	
	@Override
	public void process(Client client) {
		// TODO Auto-generated method stub

	}

}
