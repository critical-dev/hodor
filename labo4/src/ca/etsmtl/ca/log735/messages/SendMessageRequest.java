package ca.etsmtl.ca.log735.messages;

import java.io.IOException;

import ca.etsmtl.log735.model.Conversation;
import ca.etsmtl.log735.model.Group;
import ca.etsmtl.log735.model.Room;
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
			if(conversation instanceof Room){
				if(!server.getRooms().contains((Room) conversation)){
					System.out.println("SendMessageRequest : server is not aware that room " + ((Room) conversation).getName() + " exists. Aborting message send request.");
					return false;
				}
			}
			else if(conversation instanceof Group){
				if(!server.getGroupsWithConversations().containsKey((Group)conversation)){
					System.out.println("SendMessageRequest : server is not aware that group " + ((Group) conversation).getName() + " exists. Aborting message send request.");
					return false;
				}
			}
			if(server.getAuthenticatedUsers().contains(fromUser)){
				for(int i = 0 ; i < conversation.getUserlist().size(); i++){
					try {
						server.getClientsOutputStreams().get(conversation.getUserlist().get(i)).writeObject(new MessageArrived(msg, conversation));
					} catch (IOException e) {
						System.out.println("SendMessageRequest : Error occured sending to user : " + conversation.getUserlist().get(i) + " from user : " + fromUser);
						e.printStackTrace();
					}
				}
			}
			else{
				System.out.println("SendMessageRequest : user is not authenticated, aborting request.");
				return false;
			}
		}
		else{
			System.out.println("SendMessageRequest : Received null conversation, nothing to do here.");
			return false;
		}
		return true;
	}
	
}