package ca.etsmtl.ca.log735.messages;

import java.util.ArrayList;
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
 * With this message, the clients requests the creation of a group.
 * Expects a CreateGroupResponse in the response.
 * @author chrys
 *
 */
public class CreateGroupRequest extends ServerMessage {

	private static final long serialVersionUID = 3299836141471650730L;
	
	private String groupName;
	private String username = null;
	private Group createdGroup;
	private Vector<String> usersAddedToGroup;
	
	//use this constructor to create a single group with the requesting user
	//as the default user for that group
	public CreateGroupRequest(String groupName, String username, Vector<String> usersToAddToGroup){
		this.groupName = groupName;
		this.username = username;
		this.usersAddedToGroup = usersToAddToGroup;
		createdGroup = null;
	}
	
	@Override
	public boolean process(Server server) {
		for(Group group : server.getGroupsWithConversations().keySet()){
			if(group.getName().equalsIgnoreCase(groupName)){
				System.out.println("CreateGroupRequest: Group "+groupName+" already exists !! Choose a different group name. Aborting.");
				createdGroup = null;
				return false;
			}
		}
		Vector<String> defaultUsersList = new Vector<String>();
		//if a group with this name does not already exist then we create it.
		if(username != null){
			try{
				String user = server.getAuthenticatedUsers().get(server.getAuthenticatedUsers().indexOf(username));
				defaultUsersList.add(user);
				defaultUsersList.addAll(usersAddedToGroup);
			}
			catch (Exception e){
				System.out.println("CreateGroupRequest: Unable to find authenticated user for username " + username + ", aborting whole request.");
				return false;
			}
		}
		else{
			System.out.println("CreateGroupRequest: provided username cannot be null, aborting request.");
			return false;
		}
		createdGroup = new Group(groupName, defaultUsersList);
		server.getGroupsWithConversations().put(createdGroup, new ArrayList<String>());//create a new group with an empty conversation.
		System.out.println("CreateGroupRequest: Added new group " + groupName + " with [" + defaultUsersList.size() + " users]");
		return true;
	}
	
	public String getCreateGroupRequester() {
		return username;
	}

	public Vector<String> getUsersAddedToGroup() {
		return usersAddedToGroup;
	}

	public Group getGroup(){
		return createdGroup;
	}

}
