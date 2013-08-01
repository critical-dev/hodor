package ca.etsmtl.log735.model;

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

	@Override
	public String getName(){
		return groupName;
	}

}
