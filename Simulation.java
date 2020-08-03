import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Simulation implements KeyListener{
	public static Scrooge sc;

	public static void main(String[]args) {
		 JFrame frame = new JFrame("Instruction for termination");

		    JPanel panel = new JPanel();
		    panel.setLayout(new FlowLayout());

		    JLabel label = new JLabel("Press space button to terminate");
		    frame.addKeyListener(new Simulation());

		    panel.add(label);
		    //      panel.add(button);

		    frame.add(panel);
		    frame.setSize(300, 300);
		    frame.setLocationRelativeTo(null);
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.setVisible(true);
		try {
			sc = new Scrooge();
			System.out.println("Finished Initialization"); 
			int j=0;
			while(true || j==20) {
			j++;
//			Math.random() * (max - min + 1) + min
			int firstRandomNum = (int)(Math.random()*(99+1));
			int secondRandomNum = (int)(Math.random()*(99+1));
			while(firstRandomNum==secondRandomNum) {
				secondRandomNum = (int)(Math.random()*(99+1));
			}
			User firstUser = sc.usersList.get(firstRandomNum);
			User secondUser = sc.usersList.get(secondRandomNum);
			int amount = (int)(Math.random()*(10)+1);
			firstUser.payTo(secondUser, amount);
//			System.out.println("User 1 public key"+firstUser.publicKey);
//			System.out.println("User 2 public key"+secondUser.publicKey);
//			System.out.println(sc.requests.size());
			RequestedTransaction request = sc.requests.remove();
//			System.out.println(request.amount);
//			System.out.println(request.sender.publicKey);
//			System.out.println(request.receiver.publicKey);
			sc.verifyingTransactions(request);
			
//			System.out.println("User 1 public key"+request.sender.publicKey);
//			System.out.println("User 2 public key"+request.receiver.publicKey);
			}
			sc.myWriter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			System.exit(0);
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
