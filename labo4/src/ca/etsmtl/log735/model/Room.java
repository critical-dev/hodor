package ca.etsmtl.log735.model;

/**
 * Represents a room. Adds a room name to the Group class.
 * @author artom
 *
 */
public class Room extends Group {

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
