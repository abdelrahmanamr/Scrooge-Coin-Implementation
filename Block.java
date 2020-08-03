import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

public class Block {
	static int counter = 0; 
	public int ID;
	public String prevHashpointer;
	public String blockHash;
	public ArrayList<Transaction> transactions;
	public Block(int ID,Block prevBlock,ArrayList<Transaction> transactions ) {
		this.ID = ID;
		if(prevBlock!=null) {
			this.prevHashpointer = computeHash(convertBlockToString(prevBlock));
		}
		else {
			this.prevHashpointer = null;
		}
		this.transactions = transactions;
		this.blockHash = computeHash(convertBlockToString(this));
	}
	
	public static Block createBlock(Block prevBlock,ArrayList<Transaction> transactions) {
		return new Block(counter++,prevBlock,transactions);
	}
	public static String computeHash(String data) {
		
		String dataToHash = data;
		
		MessageDigest digest;
		String encoded = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
			encoded = Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return encoded;
		
	}
	public static String convertBlockToString(Block block) {
		String result = "";
		result+= block.ID;
		result+= block.prevHashpointer;
		for(int i = 0;i<block.transactions.size();i++) {
			result+= Transaction.convertTransactionToString(block.transactions.get(i));
		}
		return result;
	}
}
