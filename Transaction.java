import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class Transaction {
	static int counter = 0; 
	public int ID;
	public String prevHashpointer ;
	public int amount;
	public String senderSign;
	public int coinID;
	public User receiver;
	public PublicKey senderPublicKey;
	
	public Transaction(int ID,Transaction prevtrans,int amount,String senderSign, int coinID,PublicKey senderPublicKey) {
		this.ID = ID;
		if(prevtrans!=null) {
			this.prevHashpointer = computeHash(convertTransactionToString(prevtrans));
		}
		else {
			this.prevHashpointer = null;
		}
		this.amount = amount;
		this.senderSign = senderSign;
		this.coinID = coinID;
		this.senderPublicKey = senderPublicKey;
	}
	public Transaction(int ID,Transaction prevtrans,int amount,String senderSign, int coinID, User receiver,PublicKey senderPublicKey ) {
		this.ID = ID;
		if(prevtrans!=null) {
			this.prevHashpointer = computeHash(convertTransactionToString(prevtrans));
		}
		else {
			this.prevHashpointer = null;
		}
		this.amount = amount;
		this.senderSign = senderSign;
		this.coinID = coinID;
		this.receiver = receiver;
		this.senderPublicKey = senderPublicKey;
	}
	
//	public Transaction(int ID,User receiver,int amount,String senderSign, int coinID) {
//		this.ID = ID;
//		this.amount = amount;
//		this.senderSign = senderSign;
//		this.coinID = coinID;
//		this.receiver = receiver;
//	}
	public static Transaction createCoinTransaction(Transaction prevtrans,int amount,String senderSign,int coinID,PublicKey senderPublicKey) {
		return new Transaction(counter++,prevtrans,amount,senderSign,coinID,senderPublicKey);
	}
	public static Transaction createTransaction(Transaction prevtrans,int amount,String senderSign,int coinID,User receiver,PublicKey senderPublicKey) {
		return new Transaction(counter++,prevtrans,amount,senderSign,coinID,receiver,senderPublicKey);
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
	public static String convertTransactionToString(Transaction transaction) {
		return ""+transaction.ID+transaction.prevHashpointer+transaction.amount+transaction.senderSign+transaction.coinID+transaction.receiver+transaction.senderPublicKey;
	}
	
}
