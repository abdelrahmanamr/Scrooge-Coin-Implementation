
public class RequestedTransaction {
 public User sender;
 public User receiver;
 public int amount;
 public String senderSignature;
 
 public RequestedTransaction(User sender,User receiver,int amount,String senderSignature) {
	 this.sender = sender;
	 this.receiver = receiver;
	 this.amount = amount;
	 this.senderSignature = senderSignature;
 }
}
