

import java.util.ArrayList;
import java.util.List;

public class LockTable {
	Character item;
	String lockType;
	//RL,WL
	
	List<String> lockingTransactions = new ArrayList<String>();
	List<String> waitingTransactions = new ArrayList<String>();
	int noOfReads = 0;
	public Character getItem() {
		return item;
	}
	public void setItem(Character item) {
		this.item = item;
	}
	public String getLockType() {
		return lockType;
	}
	public void setLockType(String lockType) {
		this.lockType = lockType;
	}
	
	
	
	
}
