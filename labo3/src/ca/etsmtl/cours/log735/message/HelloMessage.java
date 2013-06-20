package ca.etsmtl.cours.log735.message;

import java.io.Serializable;
import java.util.UUID;

public class HelloMessage implements Serializable {

	private static final long serialVersionUID = 4463552101886024435L;
	
	private UUID branchId;
	private int initialMoney;
	
	public HelloMessage(UUID branchId, int initialMoney) {
		this.branchId = branchId;
		this.initialMoney = initialMoney;
	}
	
	public UUID getBranchId() {
		return branchId;
	}
	
	public int getInitialMoney() {
		return initialMoney;
	}
}
