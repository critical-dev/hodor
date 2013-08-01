package ca.etsmtl.log735.model;

import java.util.Vector;

/**
 * Represents a conversation with one or more participants.
 * @author artom
 *
 */
public class Group extends Conversation {

	private static final long serialVersionUID = 5227407559040474829L;

	private String groupName;
	
	/**
	 * Constructs a new Group with the given list of usernames.
	 * @param userlist
	 */
	public Group(String groupName, Vector<String> userlist) {
		this.groupName = groupName;
		this.userlist = userlist;
	}

	public String getGroupName(){
		return groupName;
	}

}
