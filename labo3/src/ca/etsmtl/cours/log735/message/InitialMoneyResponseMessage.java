package ca.etsmtl.cours.log735.message;

import java.util.UUID;

public class InitialMoneyResponseMessage extends Message{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3742586713190918891L;
	
	private UUID from;
	private int amount;

	public InitialMoneyResponseMessage(UUID from, int amount) {
		this.from = from;
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}
	
	public UUID getFrom() {
		return from;
	}
}
