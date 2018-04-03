package frames;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel ipLabel, portLabel;
	private JTextField txtIpAddress;
	private JTextField txtPort;
	private JButton btnJoin;
	boolean connected = false;
	Socket s;
	private final Action action = new SwingAction();
	
	/**
	 * Launch the application.
	 */
	/*
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
	
	*/
	public MainFrame(Socket _s)
	{
		this.s = _s;
		initilizeConnectionPanel();
	}
	
	/**
	 * Create the frame.
	 */
	public void initilizeConnectionPanel() 
	{
		///Set close for X button top right corner
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Set x,y,width,height
		setBounds(800, 600, 800, 600);
		//Create new JPanel for connection screen
		contentPane = new JPanel();
		//Set border
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//set content pane of this.Frame
		setContentPane(contentPane);
		//Make gridbaglayout layot type for content pane JPanel
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = 
					  new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		ipLabel = new JLabel();
		ipLabel.setText("IP Address");
		GridBagConstraints gbc_ipLabel = new GridBagConstraints();
		gbc_ipLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_ipLabel.gridx = 5;
		gbc_ipLabel.gridy = 1;
		contentPane.add(ipLabel, gbc_ipLabel);
				
		
		txtIpAddress = new JTextField();
		txtIpAddress.setText("");
		GridBagConstraints gbc_txtIpAddress = new GridBagConstraints();
		gbc_txtIpAddress.insets = new Insets(0, 0, 5, 5);
		gbc_txtIpAddress.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtIpAddress.gridx = 5;
		gbc_txtIpAddress.gridy = 2;
		contentPane.add(txtIpAddress, gbc_txtIpAddress);
		txtIpAddress.setColumns(10);
		
		portLabel = new JLabel();
		portLabel.setText("Port");
		GridBagConstraints gbc_portLabel = new GridBagConstraints();
		gbc_portLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_portLabel.gridx = 5;
		gbc_portLabel.gridy = 3;
		contentPane.add(portLabel, gbc_portLabel);
		
		txtPort = new JTextField();
		txtPort.setText("");
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
		gbc_btnJoin.gridy = 5;
		contentPane.add(btnJoin, gbc_btnJoin);
	}

	
	public void initializeGameBoardPanel()
	{
		
	}
	
	private class SwingAction extends AbstractAction 
	{
		private static final long serialVersionUID = 1L;
		public SwingAction() 
		{
			putValue(NAME, "Join");
			putValue(SHORT_DESCRIPTION, "Join the game.");
		}
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				System.out.println("Trying to connect from MainFrame Action method 'Join'");
				s.connect(new InetSocketAddress(txtIpAddress.getText(), 
												Integer.parseInt(txtPort.getText())));	
				System.out.println("Connection complete from MainFrame Action method 'Join'");
				//removeAll();
				//initializeGameBoardPanel();
			}
			catch(SocketException se)
			{
				System.out.println("From Client: " + se );
			}
			catch(NumberFormatException nfe)
			{
				System.out.println("nfe: " + nfe.getMessage() 
									+ "\nPort was not a number.");
			}
			catch(IOException ioe)
			{
				System.out.println("Reading Input Error from Client Main: " + ioe);
			}	
			
		}
	}

}
