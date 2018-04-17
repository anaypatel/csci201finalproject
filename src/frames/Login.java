package frames;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.Client;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.awt.event.ActionEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JLabel errorMsg;
	private Client c;
	
	private final Action loginAction = new LoginAction();
	private final Action signupAction = new SignUpAction();

	public Login(Client _c) {
		this.c = _c;
		initLogin();
	}
	
	public void initLogin() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(800, 600, 800, 600);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JButton Login = new JButton("Login");
		Login.setBounds(352, 323, 143, 38);
		Login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
			}
		});
		contentPane.setLayout(null);
		
		JLabel lblGameName = new JLabel("Game Name");
		lblGameName.setBounds(275, 73, 251, 82);
		lblGameName.setForeground(Color.WHITE);
		lblGameName.setFont(new Font("Lucida Grande", Font.PLAIN, 42));
		contentPane.add(lblGameName);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(275, 219, 65, 16);
		lblUsername.setForeground(Color.WHITE);
		contentPane.add(lblUsername);
		
		usernameField = new JTextField();
		usernameField.setBounds(352, 214, 143, 26);
		contentPane.add(usernameField);
		usernameField.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(275, 254, 59, 16);
		lblPassword.setForeground(Color.WHITE);
		contentPane.add(lblPassword);
		
		errorMsg = new JLabel("");
		errorMsg.setBounds(275, 282, 373, 30);
		errorMsg.setForeground(Color.RED);
		contentPane.add(errorMsg);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(352, 249, 143, 26);
		contentPane.add(passwordField);
		Login.setAction(loginAction);
		contentPane.add(Login);
		
		JButton SignUp = new JButton("Sign Up");
		SignUp.setBounds(352, 373, 143, 38);
		SignUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
			}
		});
		SignUp.setAction(signupAction);
		contentPane.add(SignUp);
	}
	
	private class LoginAction extends AbstractAction 
	{
		private static final long serialVersionUID = 1L;
		public LoginAction() 
		{
			putValue(NAME, "Login");
			putValue(SHORT_DESCRIPTION, "Login using existing info.");
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			boolean success = authenticate();	
			System.out.println("Login: " + success);
		}
		
		public boolean authenticate() {
			
			boolean authentication = true;
			
			if(usernameField.getText().equals("")) {
				System.out.println("no username");
				authentication = false;
			}
			
			if(passwordField.getPassword().length == 0) {
				System.out.println("no psswd");
				authentication = false;
			}
			
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			
			Connection conn = null;
			Statement st = null;
			java.sql.PreparedStatement ps = null;
			ResultSet rs = null;
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost/GameInfo?user=root&password=root&useSSL=false");
				st = conn.createStatement();
				ps = conn.prepareStatement("SELECT * FROM Users "
						+ "WHERE username=? "
						+ "AND password=?;");
				ps.setString(1, username);
				ps.setString(2, password);
				
				rs = ps.executeQuery();
				
				if(!rs.next()) {
					System.out.println("Username/password combination not found.");
					authentication = false;
				}
				
				while(rs.next()) {
					String usr = rs.getString("username");
					String pass = rs.getString("password");
				}
				
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			} catch (ClassNotFoundException cnfe) {
				System.out.println("cnfe: " + cnfe.getMessage());
			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
					if(st != null) {
						st.close();
					}
					if(ps != null) {
						ps.close();
					}
					if(conn != null) {
						conn.close();
					}
				} catch(SQLException sqle) {
					System.out.println("sqle closing streams: " + sqle.getMessage());
				}
			}
			
			if(authentication == false) {
				errorMsg.setText("Invalid. Try again.");
				return authentication;
			}
			
			return authentication;
		}
		
	}

	private class SignUpAction extends AbstractAction 
	{
		private static final long serialVersionUID = 1L;
		public SignUpAction() 
		{
			putValue(NAME, "Sign Up");
			putValue(SHORT_DESCRIPTION, "Sign up to save scores.");
		}
		public void actionPerformed(ActionEvent e) 
		{
			boolean success = authenticate();
			System.out.println("SignUp: " + success);
		}
		
		public boolean authenticate() 
		{
			boolean authentication = true;
			
			//username not long enough 
			if(usernameField.getText().length() < 6) {
				authentication = false;
				errorMsg.setText("Username and password must be at least 6 characters.");
				return authentication;
			}
			
			//password not long enough
			if(new String(passwordField.getPassword()).length() < 6) {
				authentication = false;
				errorMsg.setText("Username and password must be at least 6 characters.");
				return authentication;
			}
			
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			
			Connection conn = null;
			Statement st = null;
			java.sql.PreparedStatement ps = null;
			ResultSet rs = null;
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost/GameInfo?user=root&password=root&useSSL=false");
				st = conn.createStatement();
				ps = conn.prepareStatement("SELECT * FROM Users "
						+ "WHERE username=?;");
				ps.setString(1, username);
				
				rs = ps.executeQuery();
				
				if(rs.next()) {
					authentication = false;
					errorMsg.setText("Username already exists.");
					return authentication;
				}
				
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			} catch (ClassNotFoundException cnfe) {
				System.out.println("cnfe: " + cnfe.getMessage());
			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
					if(st != null) {
						st.close();
					}
					if(ps != null) {
						ps.close();
					}
					if(conn != null) {
						conn.close();
					}
				} catch(SQLException sqle) {
					System.out.println("sqle closing streams: " + sqle.getMessage());
				}
			}
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost/GameInfo?user=root&password=root&useSSL=false");
				st = conn.createStatement();
				ps = conn.prepareStatement("INSERT INTO Users (username, password) "
						+ "VALUES (?, ?)");
				ps.setString(1, username);
				ps.setString(2, password);
				ps.executeUpdate();
				
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			} catch (ClassNotFoundException cnfe) {
				System.out.println("cnfe: " + cnfe.getMessage());
			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
					if(st != null) {
						st.close();
					}
					if(ps != null) {
						ps.close();
					}
					if(conn != null) {
						conn.close();
					}
				} catch(SQLException sqle) {
					System.out.println("sqle closing streams: " + sqle.getMessage());
				}
			}
			
			return true;
			
		}
		
	}
	
}
