package ca.etsmtl.cours.log735.message;

public class TxnMessage extends Message {
	
	private static final long serialVersionUID = -9145231162432463663L;
	
	private int amount;

	public TxnMessage(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}
}
