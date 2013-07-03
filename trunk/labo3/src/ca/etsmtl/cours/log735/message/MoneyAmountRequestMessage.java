package ca.etsmtl.cours.log735.message;

import java.util.UUID;

public class MoneyAmountRequestMessage extends Message {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8632507344468670972L;
	private UUID from;

	public MoneyAmountRequestMessage(UUID from) {
		this.from = from;
	}
	public UUID getFrom() {
		return from;
	}
}
