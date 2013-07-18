package ca.etsmtl.log735.model;

import java.util.Vector;

/**
 * Represents a conversation with one or more participants.
 * @author artom
 *
 */
public class Group implements Conversation{
	
	/**
	 * The list of usernames in the Group.
	 */
	private Vector<String> userlist;
	
	
	/**
	 * Constructs a new Group with the given list of usernames.
	 * @param userlist
	 */
	public Group(Vector<String> userlist) {
		this.userlist = userlist;
	}

	/**
	 * Returns the list of usernames in the Group.
	 * @return The list of usernames in the Group.
	 */
	public Vector<String> getUserlist() {
		return userlist;
	}
}
