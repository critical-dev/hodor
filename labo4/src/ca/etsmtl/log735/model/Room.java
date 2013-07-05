package ca.etsmtl.log735.model;

public class Room extends Group {

	private String name;

	public Room(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
