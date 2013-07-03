package ca.etsmtl.cours.log735.message;

import java.util.UUID;

public class StateMessage extends Message{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8636560794949945379L;

	private String myOutput;
	private UUID id;
	
	public StateMessage(UUID id, String myOutput){
		this.id = id;
		this.myOutput = myOutput;
	}
	
	public String getOutput(){
		return myOutput;
	}
	
	public UUID getFrom(){
		return id;
	}

}
