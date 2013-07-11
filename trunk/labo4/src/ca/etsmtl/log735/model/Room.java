package ca.etsmtl.log735.model;

import java.util.Vector;

/**
 * Represents a room. Adds a room name to the Group class.
 * @author artom
 *
 */
public class Room {

	/**
	 * The name of the room.
	 */
	private String name;
	/**
	 * The list of usernames in the Room.
	 */
	private Vector<String> userlist;

	
	/**
	 * Returns the list of usernames in the Group.
	 * @return The list of usernames in the Group.
	 */
	public Vector<String> getUserlist() {
		return userlist;
	}

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
