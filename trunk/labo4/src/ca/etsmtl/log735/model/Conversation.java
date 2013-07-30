package ca.etsmtl.log735.model;

import java.io.Serializable;
import java.util.Vector;

public abstract class Conversation implements Serializable {
	
	private static final long serialVersionUID = 1210808789697869783L;
	
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
