package DB2;

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
	public List<String> getLockingTransactions() {
		return lockingTransactions;
	}
	public void setLockingTransactions(List<String> lockingTransactions) {
		this.lockingTransactions = lockingTransactions;
	}
	public List<String> getWaitingTransactions() {
		return waitingTransactions;
	}
	public void setWaitingTransactions(List<String> waitingTransactions) {
		this.waitingTransactions = waitingTransactions;
	}
	public int getNoOfReads() {
		return noOfReads;
	}
	public void setNoOfReads(int noOfReads) {
		this.noOfReads = noOfReads;
	}
	
	
	
	
}
