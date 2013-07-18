package ca.etsmtl.ca.log735.messages;

import java.io.IOException;

import ca.etsmtl.log735.model.Conversation;
import ca.etsmtl.log735.model.Group;
import ca.etsmtl.log735.model.Room;
import ca.etsmtl.log735.server.Server;

/**
 * With this message, the client requests the server to join a room.
 * The client expects a JoinRoomResponse in response.
 * @author artom
 *
 */
public class SendMessageRequest extends ServerMessage {

	private Conversation conversation;
	private String fromUser;
	private String msg;
	
	//use this constructor to send to a group of people
	public SendMessageRequest(String msg, Conversation conversation, String fromUser){
		this.conversation = conversation;
		this.msg = msg;
	}
	
	@Override
	public boolean process(Server server) {
		if(conversation != null){
			for(int i = 0 ; i < conversation.getUserlist().size(); i++){
				try {
					server.getClientsOutputStreams().get(conversation.getUserlist().get(i)).writeObject(null);
				} catch (IOException e) {
					System.out.println("Error occured sending to user : " + conversation.getUserlist().get(i) + " from user : " + fromUser);
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
}
