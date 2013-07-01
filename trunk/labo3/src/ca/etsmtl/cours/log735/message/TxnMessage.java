package ca.etsmtl.cours.log735.message;

public class TxnMessage {
	
	private int amount;

	public TxnMessage(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}
}
