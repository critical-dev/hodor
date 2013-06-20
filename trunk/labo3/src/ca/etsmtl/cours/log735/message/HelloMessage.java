package ca.etsmtl.cours.log735.message;

import java.util.UUID;

public class HelloMessage {

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
