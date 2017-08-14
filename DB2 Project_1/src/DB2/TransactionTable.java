package DB2;

import java.util.ArrayList;
import java.util.List;

public class TransactionTable {
	public String transactionID = "";
	public String transactionState = "";
	//Active, Waiting, Abort
	List<Character> lockingItems = new ArrayList<Character>();
	public int timeStamp = 1;
	
	public TransactionTable()
	{
		
	}

	public List<Character> getLockingItems() {
		return lockingItems;
	}

	public void setLockingItems(List<Character> lockingItems) {
		this.lockingItems = lockingItems;
	}

	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getTransactionState() {
		return transactionState;
	}

	public void setTransactionState(String transactionState) {
		this.transactionState = transactionState;
	}

	

	public int getTimeStamp() {
		return timeStamp;
	}

	public void setTimeSpam(int timeSpam) {
		this.timeStamp = timeStamp;
	} 
	
	 
}
