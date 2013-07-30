package ca.etsmtl.log735.model;

import java.util.Vector;

/**
 * Represents a conversation with one or more participants.
 * @author artom
 *
 */
public class Group extends Conversation{

	/**
	 * Constructs a new Group with the given list of usernames.
	 * @param userlist
	 */
	public Group(Vector<String> userlist) {
		this.userlist = userlist;
	}


}
