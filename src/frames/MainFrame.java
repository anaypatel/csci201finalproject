package frames;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.Client;

import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField txtIpAddress;
	private JTextField txtPort;
	private JButton btnJoin;
	//private String ipText = null;
	//private String  portText = null;
	boolean wait = true;
			
	
	//boolean to see if login was successful
	boolean connected = false;
	
	private final Action action = new SwingAction();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public MainFrame() {
		initUI();
	}
	
	/**
	 * Create the frame.
	 */
	public void initUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		txtIpAddress = new JTextField();
		txtIpAddress.setText("Enter IP");
		
		GridBagConstraints gbc_txtIpAddress = new GridBagConstraints();
		gbc_txtIpAddress.insets = new Insets(0, 0, 5, 5);
		gbc_txtIpAddress.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtIpAddress.gridx = 5;
		gbc_txtIpAddress.gridy = 3;
		contentPane.add(txtIpAddress, gbc_txtIpAddress);
		txtIpAddress.setColumns(10);
		
		txtPort = new JTextField();
		txtPort.setText("Enter Port");
		GridBagConstraints gbc_txtPort = new GridBagConstraints();
		gbc_txtPort.insets = new Insets(0, 0, 5, 5);
		gbc_txtPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPort.gridx = 5;
		gbc_txtPort.gridy = 4;
		contentPane.add(txtPort, gbc_txtPort);
		txtPort.setColumns(10);
		
		btnJoin = new JButton("Join");
		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnJoin.setAction(action);
		GridBagConstraints gbc_btnJoin = new GridBagConstraints();
		gbc_btnJoin.insets = new Insets(0, 0, 0, 5);
		gbc_btnJoin.gridx = 5;
		gbc_btnJoin.gridy = 6;
		contentPane.add(btnJoin, gbc_btnJoin);
	}

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Join");
			putValue(SHORT_DESCRIPTION, "Join the game.");
		}
		public void actionPerformed(ActionEvent e) {
			setWait(false);
			System.out.println("meow");
		}
	}
	
	
	public String getTxtIpAddress() {
		return txtIpAddress.getText();
	}

	public void setTxtIpAddress() {
		this.txtIpAddress.setText(null); 
	}

	public String getTxtPort() {
		return txtPort.getText();
	}

	public void setTxtPort() {
		this.txtPort.setText(null);
	}
	public void setWait(boolean condition)
	{
		this.wait = condition;
	}
	public boolean getWait()
	{
		return this.wait;
	}
	
}
