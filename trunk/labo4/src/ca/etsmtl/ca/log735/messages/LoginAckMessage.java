package ca.etsmtl.ca.log735.messages;

import java.util.List;

import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.model.Room;

public class LoginAckMessage extends ClientMessage {

	private Room defaultRoom;
	
	public LoginAckMessage(Room defaultRoom){
		this.defaultRoom = defaultRoom;
	}
	
	@Override
	public void process(Client client) {
		// TODO Auto-generated method stub
		
	}

	public Room getDefaultRoom() {
		return defaultRoom;
	}
}
