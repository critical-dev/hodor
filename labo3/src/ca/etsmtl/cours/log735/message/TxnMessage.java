package ca.etsmtl.cours.log735.message;

import java.util.UUID;

public class TxnMessage extends Message {
	
	private static final long serialVersionUID = -9145231162432463663L;
	
	private UUID from;
	private int amount;

	public TxnMessage(UUID from, int amount) {
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
