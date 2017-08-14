package DB2;


public class index {
	public static void main(String[] args) throws Exception {
		
		String fileName = "D:/IP2.txt";
		processFile file = new processFile();
		file.readFile(fileName);
		TransactionTable tt = new TransactionTable();
		
			
	}
	
}
