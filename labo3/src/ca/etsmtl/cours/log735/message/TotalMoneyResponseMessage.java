package ca.etsmtl.cours.log735.message;

import java.util.UUID;

public class TotalMoneyResponseMessage extends Message {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -746359547473199449L;
	private int amount;
	
	public TotalMoneyResponseMessage(int amount){
		this.amount = amount;
	}
	
	public int getAmount(){
		return amount;
	}

}
