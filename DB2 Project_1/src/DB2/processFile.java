package DB2;
import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;



public class processFile {
	Hashtable transaction = new Hashtable();
	Hashtable<Character, LockTable> lock = new Hashtable();
	Enumeration<String> keys_transaction;
	Enumeration<Character> keys_lock;
	int time = 1;
	

	String readFile(String path) throws IOException
	{
			String operation = "";
			try
			{
				System.out.println(path);
				File f = new File(path);
				Scanner scanner;
				try {
						scanner = new Scanner(f);
						while (scanner.hasNextLine()) 
						{
								operation =   scanner.nextLine();	
								System.out.println("---"+operation+"---");
								Character transactionID = operation.charAt(1);
								if(operation.charAt(0)=='b')
								{
									
									TransactionTable tt = new TransactionTable();
									String TID = "T" + transactionID.toString();
									tt.transactionID = "T" + transactionID.toString();
									tt.transactionState = "Active";
									tt.timeStamp = time;
									time++;
									transaction.put(TID,tt);
									System.out.println("Transaction T" + transactionID + " begins");
									
									
									
								}
								else if(operation.charAt(0)=='r')
								{
									
									Character c = '(';
									int x = operation.indexOf(c);
									Character item = operation.charAt(x+1); 
									String CurrentTransactionID = "T"+ transactionID.toString();
									
									keys_transaction = transaction.keys();
									
									
									while(keys_transaction.hasMoreElements()){
										
								        String key = keys_transaction.nextElement();
								        TransactionTable temp = (TransactionTable)transaction.get(key);
								       
								        
								        if (temp.transactionID.equals(CurrentTransactionID))
								        {
								        	if(temp.transactionState.equals("Abort"))
								        	{
								        		System.out.println("Transaction " + CurrentTransactionID + " was aborted");
								        	}
								        	else if(temp.transactionState.equals("Waiting"))
								        	{
								        		System.out.println("Transaction " + CurrentTransactionID + " is in waiting state");
								        		
								        		//Adding to waiting file
								        		addInWaiting(operation);
								        	}
								        	else if(temp.transactionState.equals("Active"))
								        	{
								        		
								        		keys_lock= lock.keys();
								        		
								        		boolean item_present = false;
												while(keys_lock.hasMoreElements()){
													
											        Character key_item = keys_lock.nextElement();
											        
											        if(key_item == item)
											        {
											        	
											        	item_present = true;
											        	LockTable temp_lock = (LockTable)lock.get(key_item);
											        	
											        	if (temp_lock.lockType.equals("RL"))
											        	{
											        		System.out.println("hits here");
											        		
											        		temp_lock.lockingTransactions.add(CurrentTransactionID);
											        		temp_lock.noOfReads = temp_lock.noOfReads + 1;
											        		lock.put(item, temp_lock);
											        		
											        		TransactionTable tt2 = (TransactionTable)transaction.get(CurrentTransactionID);
											        		tt2.lockingItems.add(item);
											        		transaction.put(tt2.transactionID, tt2);
											        		
											        		
											        		System.out.println("Transaction " + CurrentTransactionID + " has a read lock on item " + item + " with other transactions : " + temp_lock.lockingTransactions);
											        	}
											        	else if(temp_lock.lockType.equals("WL"))
											        	{
											        		//Wound wait
											        		
											        		//locking transaction--locktable entry
											        		LockTable curr_lock = (LockTable)lock.get(key_item);
											        		Iterator<String> itr_lock = curr_lock.lockingTransactions.iterator();
										        			String locking_transacton = "";
											        		while(itr_lock.hasNext())
											        		{
											        			locking_transacton = itr_lock.next();
											        			
											        		}
											        		
											        		//CurrentTransactionID
											        		TransactionTable requesting_trans = (TransactionTable)transaction.get(CurrentTransactionID);
											        		TransactionTable holding_trans = (TransactionTable)transaction.get(locking_transacton);
											        		
											        		
											        		if(requesting_trans.timeStamp < holding_trans.timeStamp)
											        		{
											        			//wound holding transaction
											        			System.out.println("Wound transaction " + holding_trans.transactionID);
											        			//release locks, state abort, execute waiting file
											        			
											        			//abort(holding_trans.transactionID);
											        			TransactionTable T_temp = (TransactionTable)transaction.get(holding_trans.transactionID);
											        			T_temp.transactionState = "Abort";
											        			transaction.put(T_temp.transactionID, T_temp);
											        			System.out.println("Tranasaction "+T_temp.transactionID+" is Aborted");
											        			
											        			//release locks and execute waiting
											        			releaseLocks(holding_trans.transactionID);
											        		}
											        		else
											        		{
											        			
											        			//wait requesting timespam....insert into file
											        			TransactionTable T_temp2 = (TransactionTable)transaction.get(requesting_trans.transactionID);
											        			T_temp2.transactionState = "Waiting";
											        			transaction.put(T_temp2.transactionID, T_temp2);
											        			
											        			LockTable lt2 = (LockTable)lock.get(item);
											        			lt2.waitingTransactions.add(CurrentTransactionID);
											        			lock.put(item, lt2);
											        			addInWaiting(operation);
											        			System.out.println("Tranasaction "+T_temp2.transactionID+" is in waiting for item" + item);
											        			
											        		}
											        		
											        	}
											        	
											        	
											        }
												}    
								        		if (!item_present)
								        		{
								        			LockTable lt = new LockTable();
								        			lt.item= item;
								        			lt.lockingTransactions.add( "T" + transactionID.toString());
								        			lt.lockType = "RL";
								        			lt.noOfReads = 1;
								        			lock.put(item, lt);
								        			
								        			//put in trnasaction table locking variables
								        			TransactionTable T_temp2 = (TransactionTable)transaction.get(CurrentTransactionID);
								        			T_temp2.lockingItems.add(item);
								        			transaction.put(CurrentTransactionID, T_temp2);
								        			
								        			
								        			System.out.println("Transaction------ " + CurrentTransactionID + " has a read lock on item " + item);
								        			
								        		}
								        									        	
								        	}
								        	     	
								        }
								    }
									
								}
								else if(operation.charAt(0)=='w')
								{
									Character c = '(';
									int x = operation.indexOf(c);
									Character item = operation.charAt(x+1); 
									
									String currentTransactionID = "T" + transactionID.toString();
									TransactionTable currentTransaction = (TransactionTable)transaction.get(currentTransactionID);
									
									if(currentTransaction.transactionState.equals("Abort"))
						        	{
						        		System.out.println("Transaction " + currentTransactionID + " was aborted");
						        	}
						        	else if(currentTransaction.transactionState.equals("Waiting"))
						        	{
						        		System.out.println("Transaction " + currentTransactionID + " is in waiting state");
						        		
						        		//Adding to waiting file
						        		addInWaiting(operation);
						        				
				        			}
						        	else if(currentTransaction.transactionState.equals("Active"))
						        	{
						        		keys_lock= lock.keys();
						        		
						        		boolean item_present = false;
										while(keys_lock.hasMoreElements()){
											
									        Character key_item = keys_lock.nextElement();
									        
									        if(key_item == item)
									        {
									        	
									        	item_present = true;
									        	LockTable temp_lock = (LockTable)lock.get(key_item);
									        	
									        	if (temp_lock.lockType.equals("RL"))
									        	{
									        		Iterator<String> locking_trans = temp_lock.lockingTransactions.iterator();
									        		String locking_transaction = "";
									        		while(locking_trans.hasNext())
									        		{
									        			locking_transaction = locking_trans.next();
									        		}
									        		
									        		
									        		if(temp_lock.noOfReads == 1 && currentTransactionID.equals(locking_transaction))
									        		{
									        			temp_lock.lockType = "WL";
									        			lock.put(temp_lock.item, temp_lock);
									        			System.out.println("Transaction " + currentTransactionID + " has upgraded the lock to write lock on item " + item);
									        		}
									        		else
									        		{
									        			//add in waiting state
									        			TransactionTable temp_trans = (TransactionTable)transaction.get(currentTransactionID);
									        			temp_trans.transactionState = "Waiting";
									        			transaction.put(currentTransactionID, temp_trans);
									        			
									        			temp_lock.waitingTransactions.add(currentTransactionID);
									        			lock.put(temp_lock.item, temp_lock);
									        			addInWaiting(operation);
									        			System.out.println("Transaction " + currentTransactionID + " is now in waiting state " );
									        		}
									        	}
									        	else if(temp_lock.lockType.equals("WL"))
									        	{
									        		//Wound wait
									        		
									        		//locking transaction--locktable entry
									        		LockTable curr_lock = (LockTable)lock.get(key_item);
									        		Iterator<String> itr_lock = curr_lock.lockingTransactions.iterator();
								        			String locking_transacton = "";
									        		while(itr_lock.hasNext())
									        		{
									        			locking_transacton = itr_lock.next();
									        			
									        		}
									        		
									        		//CurrentTransactionID
									        		TransactionTable requesting_trans = (TransactionTable)transaction.get(currentTransactionID);
									        		TransactionTable holding_trans = (TransactionTable)transaction.get(locking_transacton);
									        		
									        		
									        		
									        		
									        		
									        		if(requesting_trans.timeStamp < holding_trans.timeStamp)
									        		{
									        			//wound holding transaction
									        			System.out.println("Wound transaction " + holding_trans.transactionID);
									        			//release locks, state abort, execute waiting file
									        			
									        			//abort(holding_trans.transactionID);
									        			TransactionTable T_temp = (TransactionTable)transaction.get(holding_trans.transactionID);
									        			T_temp.transactionState = "Abort";
									        			transaction.put(T_temp.transactionID, T_temp);
									        			System.out.println("Tranasaction "+T_temp.transactionID+" is Aborted");
									        			
									        			//release locks and execute waiting
									        			releaseLocks(holding_trans.transactionID);
									        		}
									        		else
									        		{
									        			System.out.println("in wait condition " );
									        			//wait requesting timespam....insert into file
									        			TransactionTable T_temp2 = (TransactionTable)transaction.get(requesting_trans.transactionID);
									        			T_temp2.transactionState = "Waiting";
									        			transaction.put(T_temp2.transactionID, T_temp2);
									        			
									        			LockTable lt2 = (LockTable)lock.get(item);
									        			lt2.waitingTransactions.add(currentTransactionID);
									        			lock.put(item, lt2);
									        			addInWaiting(operation);
									        			System.out.println("Tranasaction "+T_temp2.transactionID+" is in waiting for item" + item);
									        			
									        		}
									        		
									        	}
									        	
									        	
									        }
										}    
						        		if (!item_present)
						        		{
						        			LockTable lt = new LockTable();
						        			lt.item= item;
						        			lt.lockingTransactions.add( "T" + transactionID.toString());
						        			lt.lockType = "WL";
						        			lt.noOfReads = 1;
						        			lock.put(item, lt);
						        			
						        			//put in trnasaction table locking variables
						        			TransactionTable T_temp3 = (TransactionTable)transaction.get(currentTransactionID);
						        			T_temp3.lockingItems.add(item);
						        			transaction.put(currentTransactionID, T_temp3);
						        			
						        			
						        			System.out.println("Transaction------ " + currentTransactionID + " has a write lock on item " + item);
						        			
						        		}
						        		
						        	}
									
									
									
								}
								else if(operation.charAt(0)=='c' ||operation.charAt(0)=='e' )
								{
									String currentTransactionID = "T" + transactionID.toString();
									
									TransactionTable T_end = (TransactionTable)transaction.get(currentTransactionID);
				        			if(!T_end.transactionState.equals("Abort"))
				        			{
				        				T_end.transactionState = "Abort";
				        				transaction.put(T_end.transactionID, T_end);
				        				System.out.println("Tranasaction "+T_end.transactionID+" is Committed");
				        			
				        				//release locks and execute waiting
				        				releaseLocks(currentTransactionID);
				        			}
				        			else
				        			{
				        				System.out.println("Tranasaction "+T_end.transactionID+" is Aaborted");
				        			}
								}
								else
								{
									System.out.println("Invalid line");
								}								
						}
					} 
				catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}					
			}
			catch(Exception e)
			{
				
			}
			
			
			return operation;
		}
	public void releaseLocks(String Trans_ID) throws IOException
	{
		// release locks, execute waiting
		
		//release locks -- 
		TransactionTable T_temp2 = (TransactionTable)transaction.get(Trans_ID);
		Iterator<Character> itr = T_temp2.lockingItems.iterator();
		LockTable lock_release;
		while(itr.hasNext())
		{
			Character current_item = itr.next();
			
			lock_release = (LockTable)lock.get(current_item);
			lock_release.lockingTransactions.remove(Trans_ID);
			
			Iterator<String> itr_sub = lock_release.waitingTransactions.iterator(); 
			
			while(itr_sub.hasNext())
			{
				String tr = itr_sub.next();
				
				TransactionTable T_temp3 = (TransactionTable)transaction.get(tr);
				if(!T_temp3.transactionState.equals("Abort"))
				{
					T_temp3.transactionState = "Active";
					transaction.put(tr, T_temp3);
				}
			}
			
			lock_release.noOfReads = lock_release.noOfReads - 1;
			
			lock.remove(lock_release.item);
			
		}
		readFile("D:/WaitingOperations.txt");
				
	}
	public void addInWaiting(String operation) throws FileNotFoundException, UnsupportedEncodingException
	{
		
		PrintWriter writer = new PrintWriter("D:/WaitingOperations.txt", "UTF-8");
		File f_waiting = new File("D:/WaitingOperations.txt");
		Scanner readwaiting;
		boolean flag = false;
		try {
			readwaiting = new Scanner(f_waiting);
			while (readwaiting.hasNextLine()) 
			{
				String currentline = readwaiting.nextLine();
				writer.println(currentline);
				if(operation.equals(currentline))
				{
					flag = true;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		if(!flag)
		{
			writer.println(operation);
		}
		writer.close();	
	}	
}
