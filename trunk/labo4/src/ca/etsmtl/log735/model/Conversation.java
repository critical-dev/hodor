package ca.etsmtl.log735.model;

import java.io.Serializable;
import java.util.Vector;
/******************************************************
Cours : LOG735
Session : Été 2013
Groupe : 01
Laboratoire : Laboratoire #4
Étudiants : Artom Lifshitz, Chrystophe Chabert
Code(s) perm. : LIFA29108505, CHAC12098902
Date création : 01/07/2013
******************************************************/
public abstract class Conversation implements Serializable {
	
	private static final long serialVersionUID = 1210808789697869783L;
	
	/**
	 * The list of usernames in the Group.
	 */
	protected Vector<String> userlist = new Vector<String>();
	
	/**
	 * Returns the list of usernames in the Group.
	 * @return The list of usernames in the Group.
	 */
	public synchronized Vector<String> getUserlist() {
		return userlist;
	}
	
	public abstract String getName();
}
