package ca.etsmtl.cours.log735.message;

import java.util.UUID;

/**
 * Object message to indicate to start a capture.
 * 
 * */
public class StateSyncStartMessage extends Message {
	
	private static final long serialVersionUID = -9145231162432463663L;
	
	private UUID from;
	
	public StateSyncStartMessage(UUID from) {
		this.from = from;
	}
	public UUID getFrom() {
		return from;
	}
}
