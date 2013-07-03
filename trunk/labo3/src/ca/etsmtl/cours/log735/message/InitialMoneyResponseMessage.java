package ca.etsmtl.cours.log735.message;

import java.util.UUID;

public class InitialMoneyResponseMessage extends Message {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -746359547473199449L;
	private int amount;
	private UUID id;
	
	public InitialMoneyResponseMessage(UUID id, int amount){
		this.amount = amount;
		this.id = id;
	}
	
	public UUID getFrom(){
		return id;
	}
	
	public int getAmount(){
		return amount;
	}

}
