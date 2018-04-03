package frames;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class MainFrame extends JFrame
{
	 public MainFrame() {

	        initUI();
	    }
	 
	 private void initUI() {

	        add(new Board());

	        setSize(400, 400);

	        setTitle("Application");
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setLocationRelativeTo(null);
	    }    
}
