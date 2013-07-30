package ca.etsmtl.log735.model;

import java.util.Vector;

public abstract class Conversation {
	
	/**
	 * The list of usernames in the Group.
	 */
	protected Vector<String> userlist;
	
	/**
	 * Returns the list of usernames in the Group.
	 * @return The list of usernames in the Group.
	 */
	public Vector<String> getUserlist() {
		return userlist;
	}
}
