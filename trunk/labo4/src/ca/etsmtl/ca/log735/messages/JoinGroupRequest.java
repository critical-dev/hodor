package ca.etsmtl.ca.log735.messages;

import ca.etsmtl.log735.model.Group;
import ca.etsmtl.log735.model.Room;
import ca.etsmtl.log735.server.Server;

/**
 * With this message, the client requests the server to join a room.
 * The client expects a JoinRoomResponse in response.
 * @author artom
 *
 */
public class JoinGroupRequest extends ServerMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2521285633403217157L;
	private String groupName;
	private String username;
	private Group group;
	
	//the username is required so it can be added to the room's userlist.
	public JoinGroupRequest(String groupName, String username){
		this.groupName = groupName;
		this.username = username.toLowerCase();
		group = null;
	}
	
	@Override
	public boolean process(Server server) {
		for(Group group: server.getGroupsWithConversations().keySet()){
			if(group.getGroupName().equalsIgnoreCase(groupName)){
				if(!group.getUserlist().contains(username)){
					try{
						String user = server.getAuthenticatedUsers().get(server.getAuthenticatedUsers().indexOf(username));
						if(!group.getUserlist().contains(user)){
							group.getUserlist().add(user);
							return true;
						}
						else{
							System.err.println("JoinGroupRequest: user is already part of group " + groupName + ", not adding it.");
							return false;
						}
					}
					catch (Exception e){
						System.err.println("Server was unable to retrieve authenticated user for username " + username + ". Could not add to group.");
						return false;
					}
				}				
			}
		}
		System.out.println("JoinGroupRequest : group " + groupName + " does not exist. Create group first.");
		return false;
	}
	
	public Group getGroup(){
		return group;
	}

}
