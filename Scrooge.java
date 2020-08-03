import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;


public class Scrooge {
	public ArrayList<Block> ledger;
	public ArrayList<Transaction> accumlatedTrans;
	public ArrayList<Coin> createdCoins;
	public ArrayList<User> usersList;
	public PublicKey publicKey;
	public PrivateKey privateKey;
	public FileWriter myWriter;
	public static Queue<RequestedTransaction> requests;
	public ScroogePointer scroogePointer;
	
	public Scrooge() throws Exception {
		myWriter = new FileWriter("output.txt");
		this.ledger = new ArrayList<Block>();
		this.accumlatedTrans = new ArrayList<Transaction>();
		this.createdCoins = new ArrayList<Coin>();
		this.usersList = new ArrayList<User>();
		KeyPair keypair = generateKeyPair();
		this.publicKey = keypair.getPublic();
		this.privateKey = keypair.getPrivate();
		this.requests = new LinkedList<RequestedTransaction>();
		initializeNetwork();
	}
	
	public void initializeNetwork() throws Exception {
		
//		myWriter.close();
		User user = new User();
		this.usersList.add(user);
		for(int i = 0;i<1000;i++) {
			if(i%10==0 && i!=0) {
//				System.out.println("User "+ ((i/10))+" Public key: "+user.publicKey);
//				System.out.println("User "+ ((i/10))+" Coin Balance: "+user.coins.size());
				user =  new User();
				this.usersList.add(user);
			}
			Coin coin = Coin.createCoin();
			createdCoins.add(coin);
			Transaction trans = Transaction.createCoinTransaction(null, 1, sign(this.privateKey),coin.ID,this.publicKey);
			this.accumlatedTrans.add(trans);
			myWriter.write("Create Coin Transaction "+trans.ID+" Data( ID: "+trans.ID+" ,Hash of the previous transaction: "+trans.prevHashpointer+" ,amount: "+trans.amount+" ,sign of the sender: "+trans.senderSign+" , ID of coin: "+trans.coinID+" )"+"\n");
			myWriter.write("\n");
			myWriter.write("Block under construction transactions:"+"\n");
			for(int j = 0;j<this.accumlatedTrans.size();j++) {
				myWriter.write("Transaction ID:"+this.accumlatedTrans.get(j).ID+"\n");
			}
			myWriter.write("\n");
			Transaction distributeCoinTrans = Transaction.createTransaction(trans, 1, sign(this.privateKey),coin.ID,user,this.publicKey);
			this.accumlatedTrans.add(distributeCoinTrans);
			myWriter.write("Distribute Coin Transaction "+distributeCoinTrans.ID+" Data( ID: "+distributeCoinTrans.ID+" ,Hash of the previous transaction: "+distributeCoinTrans.prevHashpointer+" ,amount: "+distributeCoinTrans.amount+" ,sign of the sender: "+distributeCoinTrans.senderSign+" , ID of coin: "+distributeCoinTrans.coinID+", Receiver public key: "+distributeCoinTrans.receiver.publicKey +" )"+"\n");
			myWriter.write("\n");
			myWriter.write("Block under construction transactions:"+"\n");
			for(int j = 0;j<this.accumlatedTrans.size();j++) {
				myWriter.write("Transaction ID:"+this.accumlatedTrans.get(j).ID+"\n");
			}
			myWriter.write("\n");
			if(this.accumlatedTrans.size()==10) {
				
				if(this.ledger.size()==0) {
					Block block = Block.createBlock(null, this.accumlatedTrans);
					this.ledger.add(block);
					this.scroogePointer= new ScroogePointer(Block.computeHash(Block.convertBlockToString(block)), sign(this.privateKey));
					myWriter.write("Blockchain after creation of Block "+ block.ID +"\n");
					for(int j =0;j<this.ledger.size();j++) {
						myWriter.write("Block "+j+" Data( ID: " +this.ledger.get(j).ID+" ,Hash of the block: "+this.ledger.get(j).blockHash+" ,Hash of the previous block: "+this.ledger.get(j).prevHashpointer +" )"+"\n");
					}
					myWriter.write("Scrooge pointer value "+ scroogePointer.scroogePointerValue+"\n");
					myWriter.write("\n");
		
				}
				else {
					Block block = Block.createBlock(this.ledger.get(this.ledger.size()-1), this.accumlatedTrans);
					this.ledger.add(block);
					this.scroogePointer.scroogePointerValue = Block.computeHash(Block.convertBlockToString(block));
					myWriter.write("\n");
					myWriter.write("Blockchain after creation of Block "+ block.ID +"\n");
					for(int j =0;j<this.ledger.size();j++) {
						myWriter.write("Block "+j+" Data( ID: " +this.ledger.get(j).ID+" ,Hash of the block: "+this.ledger.get(j).blockHash+" ,Hash of the previous block: "+this.ledger.get(j).prevHashpointer +" )"+"\n");
					}
					myWriter.write("Scrooge pointer value "+ scroogePointer.scroogePointerValue+"\n");
					myWriter.write("\n");
				}
				for(int j=0;j<this.createdCoins.size();j++) {
					user.coins.add(this.createdCoins.get(j));
				}
				myWriter.write("User "+ ((i/10))+" Public key: "+user.publicKey+"\n");
				myWriter.write("User "+ ((i/10))+" Coin Balance: "+user.coins.size()+"\n");
				myWriter.write("\n");
				this.accumlatedTrans = new ArrayList<Transaction>();
				
				this.createdCoins= new ArrayList<Coin>(); 
			}
		}
		this.accumlatedTrans=new ArrayList<Transaction>();
		
	}
	
	
	public static KeyPair generateKeyPair() throws Exception {
	    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
	    generator.initialize(2048, new SecureRandom());
	    KeyPair pair = generator.generateKeyPair();
	    return pair;
	}
	
	public static String sign(PrivateKey privateKey) throws Exception {
	    Signature privateSignature = Signature.getInstance("SHA256withRSA");
	    privateSignature.initSign(privateKey);
	    byte[] signature = privateSignature.sign();
	    return Base64.getEncoder().encodeToString(signature);
	}
	
	public static boolean verify(String signature, PublicKey publicKey) throws Exception {
	    Signature publicSignature = Signature.getInstance("SHA256withRSA");
	    publicSignature.initVerify(publicKey);
	    byte[] signatureBytes = Base64.getDecoder().decode(signature);
	    return publicSignature.verify(signatureBytes);
	}
	
	public void verifyingTransactions(RequestedTransaction trans) throws Exception {
			if(trans.sender.coins.size()>=trans.amount) {
				if(verify(trans.senderSignature, trans.sender.publicKey)) {
					boolean owner = true;
					TreeSet<Integer> coinsList = new TreeSet<Integer>();
					for(int i=0;i<trans.amount;i++) { // checking that the sender is the owner of the coins
						Coin currentCoin = trans.sender.coins.get(i);
						coinsList.add(currentCoin.ID);
							for(int j=this.ledger.size()-1;j>=0;j--) {
								Block currentBlock = this.ledger.get(j);
								for(int k = currentBlock.transactions.size()-1;k>=0;k--) {
									if(currentBlock.transactions.get(k).coinID==currentCoin.ID) {
										if(currentBlock.transactions.get(k).receiver.publicKey!=trans.sender.publicKey) {
											owner=false;
											i=trans.amount;
										}
										k=-1;
										j=-1;
									}
								}
							}
					}
//					System.out.println(coinsList.size());
//					System.out.println(trans.amount);
					if(owner && this.verifyBlockChain() && coinsList.size()==trans.amount) { //check the ownership of coin and the block chain is correct and the coins is not double spent
						for(int i=0;i<trans.amount;i++) { // checking that the sender is the owner of the coins
							Coin currentCoin = trans.sender.coins.get(0);
								for(int j=this.ledger.size()-1;j>=0;j--) {
									Block currentBlock = this.ledger.get(j);
									for(int k = currentBlock.transactions.size()-1;k>=0;k--) {
										if(currentBlock.transactions.get(k).coinID==currentCoin.ID) {
											Transaction transaction = Transaction.createTransaction(currentBlock.transactions.get(k), 1, trans.senderSignature, currentCoin.ID, trans.receiver,trans.sender.publicKey);
											trans.receiver.coins.add(trans.sender.coins.get(0));
											trans.sender.coins.remove(0);
											this.accumlatedTrans.add(transaction);
											myWriter.write("Payment Transaction "+transaction.ID+" Data( ID: "+transaction.ID+" ,Hash of the previous transaction: "+transaction.prevHashpointer+" ,amount: "+transaction.amount+" ,sign of the sender: "+transaction.senderSign+" , ID of coin: "+transaction.coinID+", Receiver public key: "+transaction.receiver.publicKey +" )"+"\n");
											myWriter.write("\n");
											myWriter.write("Block under construction transactions:"+"\n");
											for(int l = 0;l<this.accumlatedTrans.size();l++) {
												myWriter.write("Transaction ID:"+this.accumlatedTrans.get(l).ID+"\n");
											}
											myWriter.write("\n");
											if(this.accumlatedTrans.size()==10) { // checking for double spending
												for(int l=0;l<this.accumlatedTrans.size()-1;l++) {
													for(int m=l+1;m<this.accumlatedTrans.size();m++) {
														if(this.accumlatedTrans.get(l).coinID==this.accumlatedTrans.get(m).coinID && this.accumlatedTrans.get(l).senderSign==this.accumlatedTrans.get(m).senderSign) {
															for(int n=0;n<this.accumlatedTrans.get(m).receiver.coins.size();n++) {
																if(this.accumlatedTrans.get(m).receiver.coins.get(n).ID==this.accumlatedTrans.get(m).coinID) {
																	this.accumlatedTrans.get(m).receiver.coins.remove(n);
																}
															}
															this.accumlatedTrans.remove(m);
															m--;
														}
													}
												}
												if(this.accumlatedTrans.size()==10) {
													Block block = Block.createBlock(this.ledger.get(this.ledger.size()-1), this.accumlatedTrans);
													this.ledger.add(block);
													this.scroogePointer.scroogePointerValue = Block.computeHash(Block.convertBlockToString(block));
													myWriter.write("Blockchain after creation of Block "+ block.ID +"\n");
													for(int c =0;c<this.ledger.size();c++) {
														myWriter.write("Block "+c+" Data( ID: " +this.ledger.get(c).ID+" ,Hash of the block: "+this.ledger.get(c).blockHash+" ,Hash of the previous block: "+this.ledger.get(c).prevHashpointer +" )"+"\n");
													}
													myWriter.write("Scrooge pointer value "+ scroogePointer.scroogePointerValue+"\n");
													this.accumlatedTrans = new ArrayList<Transaction>();
												}
											}
											k=-1;
											j=-1;
										}
									}
								}
						}
					}
				}
			}
			else {
				for(int i = 0;i<this.accumlatedTrans.size();i++) {
					Transaction currentTrans = this.accumlatedTrans.get(i);
					if(currentTrans.senderPublicKey==trans.sender.publicKey) {
						myWriter.write("Double spending transaction is detected:"+"\n");
						myWriter.write("Payment Transaction from user with public key:"+trans.sender.publicKey+"\n");
						myWriter.write("To user with public key:"+trans.receiver.publicKey+"\n");
						myWriter.write("With amount:"+trans.amount+"\n");
						myWriter.write("\n");
						System.out.println("double spending occurs");
						i=this.accumlatedTrans.size();
					}
				}
			}
	}
	public boolean verifyBlockChain() throws Exception {
//		System.out.println("da5al");
		if(!scroogePointer.scroogePointerValue.equals(Block.computeHash(Block.convertBlockToString(this.ledger.get(this.ledger.size()-1)))) || !verify(scroogePointer.scroogeSignature, this.publicKey)) {
			return false;
		}
		for(int i=this.ledger.size()-1;i>0;i--) {
			if(!this.ledger.get(i).prevHashpointer.equals(Block.computeHash(Block.convertBlockToString(this.ledger.get(i-1))))) {
				return false;
			}
		}
		return true;
	}
	public static void main(String[] args) {
//		try {
//			Scrooge sc = new Scrooge();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
}
