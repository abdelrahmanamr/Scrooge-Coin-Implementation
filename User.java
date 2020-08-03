import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;


public class User {
	public PublicKey publicKey;
	public PrivateKey privateKey;
	public ArrayList<Coin>coins;
	
	public User() throws Exception {
		KeyPair keypair = generateKeyPair();
		this.publicKey = keypair.getPublic();
		this.privateKey = keypair.getPrivate();
		this.coins = new ArrayList<Coin>();
	}
	
	
	
	public static KeyPair generateKeyPair() throws Exception {
	    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
	    generator.initialize(2048, new SecureRandom());
	    KeyPair pair = generator.generateKeyPair();
	    return pair;
	}
	
	public void payTo(User receiver, int amount) throws Exception {
		Scrooge.requests.add(new RequestedTransaction(this,receiver, amount,this.sign(this.privateKey)));
	}
	public static String sign(PrivateKey privateKey) throws Exception {
	    Signature privateSignature = Signature.getInstance("SHA256withRSA");
	    privateSignature.initSign(privateKey);
	    byte[] signature = privateSignature.sign();
	    return Base64.getEncoder().encodeToString(signature);
	}
}
