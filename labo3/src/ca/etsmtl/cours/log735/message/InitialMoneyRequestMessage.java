package ca.etsmtl.cours.log735.message;

import java.util.UUID;

public class InitialMoneyRequestMessage extends Message{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3852882976154252485L;
	private UUID fromId;
	
	public InitialMoneyRequestMessage(UUID fromId){
		this.fromId = fromId;
	}
	
	public UUID getFrom(){
		return fromId;
	}

}
