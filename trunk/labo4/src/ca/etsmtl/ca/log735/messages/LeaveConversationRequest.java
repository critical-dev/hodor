package ca.etsmtl.ca.log735.messages;

import java.io.IOException;
import java.util.Vector;

import ca.etsmtl.log735.model.Group;
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
 * Removes a user from a group
 * @author chrys
 *
 */
public class LeaveConversationRequest extends ServerMessage {

	private static final long serialVersionUID = 2521285633403217157L;

	private String conversation;
	private String username;

	public LeaveConversationRequest(String conversation, String username){
		this.conversation = conversation;
		this.username = username.toLowerCase();
	}

	@Override
	public boolean process(Server server) {
		for(Group group: server.getGroupsWithConversations().keySet()){
			if(group.getName().equalsIgnoreCase(conversation)){
				if(group.getUserlist().contains(username)){
					group.getUserlist().remove(username);
					System.out.println("LeaveConversationRequest: user " + username + " removed from group.");
					//we also destroy the group if there's nobody left in it.
					if(group.getUserlist().size() == 0){
						System.out.println("LeaveConversationRequest: no more users in group, removed it.");
						server.getGroupsWithConversations().remove(group);
					}
					else{
						Vector<String> newGroupMembersToNotify = group.getUserlist();
						for(int i = 0; i < newGroupMembersToNotify.size(); i++){
							for(String user : server.getClientsOutputStreams().keySet()){
								if(newGroupMembersToNotify.get(i).equalsIgnoreCase(user)){
									try {
										System.out.println("LeaveConversationRequest: Notified "+user+" that group has one less member.");
										server.getClientsOutputStreams().get(user).reset();
										server.getClientsOutputStreams().get(user).writeObject(new RefreshUserListResponse(group));
									} catch (IOException e) {
										e.printStackTrace();
									}
									break;
								}
							}
						}
					}

				}
				else{
					System.out.println("LeaveConversationRequest: user " + username + " not part of that group. No effect.");
				}
				return true;
			}
		}
		int indexOfRoom  = -1;
		//if it wasn't a group try rooms
		for(int i = 0; i < server.getRooms().size(); i++){
			if(server.getRooms().get(i).getName().equals(conversation)){
				indexOfRoom = i;
				break;
			}
		}
		if(indexOfRoom != -1){
			if(server.getRooms().get(indexOfRoom).getUserlist().contains(username)){
				server.getRooms().get(indexOfRoom).getUserlist().remove(username);
				System.out.println("LeaveConversationRequest: Successfully removed user "+username+" from room "+conversation);
				//if no more users in that room we delete it, except for the default room.
				if(server.getRooms().get(indexOfRoom) != server.getDefaultRoom() && server.getRooms().get(indexOfRoom).getUserlist().size() == 0){
					System.out.println("LeaveConversationRequest: No more users in that room, removed it. ");
					server.getRooms().remove(indexOfRoom);
				}
				else{
					Vector<String> newGroupMembersToNotify = server.getRooms().get(indexOfRoom).getUserlist();
					for(int i = 0; i < newGroupMembersToNotify.size(); i++){
						for(String user : server.getClientsOutputStreams().keySet()){
							if(newGroupMembersToNotify.get(i).equalsIgnoreCase(user)){
								try {
									System.out.println("LeaveConversationRequest: Notified "+user+" that room has one less member.");
									server.getClientsOutputStreams().get(user).reset();
									server.getClientsOutputStreams().get(user).writeObject(new RefreshUserListResponse(server.getRooms().get(indexOfRoom)));
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							}
						}
					}
				}
			}
			else{
				System.out.println("LeaveConversationRequest: user "+username+" not in that room. No effect.");
			}
		}
		else{
			System.err.println("LeaveConversationRequest: can't remove user from room "+conversation+", room doesn't exist.");
			return false;
		}
		System.err.println("LeaveConversationRequest: conversation " + conversation + " doesn't exist, can't remove user. No effect.");
		return false;
	}
}
