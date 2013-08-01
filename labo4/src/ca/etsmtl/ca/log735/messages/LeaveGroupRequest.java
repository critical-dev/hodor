package ca.etsmtl.ca.log735.messages;

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
 * Removes a user from a group
 * @author chrys
 *
 */
public class LeaveGroupRequest extends ServerMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2521285633403217157L;
	private String groupName;
	private String username;
	
	public LeaveGroupRequest(String groupName, String username){
		this.groupName = groupName;
		this.username = username.toLowerCase();
	}
	
	@Override
	public boolean process(Server server) {
		for(Group group: server.getGroupsWithConversations().keySet()){
			if(group.getGroupName().equalsIgnoreCase(groupName)){
				if(group.getUserlist().contains(username)){
					group.getUserlist().remove(username);
					System.out.println("LeaveGroupRequest: user " + username + " removed from group.");
					//we also destroy the group if there's nobody left in it.
					if(group.getUserlist().size() == 0){
						server.getGroupsWithConversations().remove(group);
					}
				}
				else{
					System.out.println("LeaveGroupRequest: user " + username + " not part of that group. No effect.");
				}
				return true;
			}
		}
		System.err.println("LeaveGroupRequest: group " + groupName + " doesn't exist, can't remove user. No effect.");
		return false;
	}
}
