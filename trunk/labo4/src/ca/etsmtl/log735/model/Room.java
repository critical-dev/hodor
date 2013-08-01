package ca.etsmtl.log735.model;

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
 * Represents a room. Adds a room name to the Group class.
 * @author artom
 *
 */
public class Room extends Conversation {

	private static final long serialVersionUID = 3861101023454396689L;
	
	/**
	 * The name of the room.
	 */
	private String name;

	/**
	 * Constructs a room with the given name.
	 * @param name The name of the new room.
	 */
	public Room(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the room.
	 * @return The name of the room.
	 */
	public String getName() {
		return name;
	}
	
	@Override
	/**
	 * Returns the name of the room.
	 * @return The name of the room.
	 */
	public String toString() {
		return name;
	}
}
