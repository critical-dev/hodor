package ca.etsmtl.log735.model;

public class Message {
	
	private Conversation conv;
	private String fromUser;
	private String message;
	
	public Message(Conversation conv, String fromUser, String message) {
		this.conv = conv;
		this.fromUser = fromUser;
		this.message = message;
	}

	public Conversation getConv() {
		return conv;
	}

	public String getFromUser() {
		return fromUser;
	}

	public String getMessage() {
		return message;
	}
}
