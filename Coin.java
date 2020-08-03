import java.util.TreeSet;

public class Coin {
	static int counter = 0; 
	public int ID;
	public Coin(int id) {
		this.ID = id;
	}
	public static Coin createCoin() {
		return new Coin(counter++);
	}
	public static void main(String args[]) {
		Coin one = createCoin();
		Coin two = createCoin();
		TreeSet<Integer> list = new TreeSet<Integer>();
		list.add(1);
		list.add(2);
		list.add(1);
		System.out.println(one.hashCode());
		System.out.println(one);
//		System.out.println(list.size());
//		System.out.println(one.ID);
//		System.out.println(two.ID);
	}
}
