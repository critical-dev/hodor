package ca.etsmtl.cours.log735.message;

import java.util.UUID;

/**
 * Object message to indicate whether to start capturing or not.
 * 
 * */
public class StateSyncStopMessage extends Message {
	
	private static final long serialVersionUID = -9145231162432463663L;
	
	private UUID from;
	
	public StateSyncStopMessage(UUID from) {
		this.from = from;
	}
	public UUID getFrom() {
		return from;
	}
}
