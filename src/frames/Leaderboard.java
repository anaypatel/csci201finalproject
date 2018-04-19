package frames;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

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
import javax.swing.JScrollPane;
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
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import java.awt.ScrollPane;

public class Leaderboard extends JPanel {

//	private JPanel contentPane;
	private Client c;

	private final Action signoutAction = new SignOutAction();
	private final Action joinAction = new JoinAction();
	private JTable leaderboard;
	
	public Leaderboard(Client _c) {
		this.c = _c;
		initLogin();
	}
	
	public void initLogin() {
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(800, 600, 800, 600);
//		contentPane = new JPanel();
//		setContentPane(contentPane);
		this.setBackground(Color.BLACK);
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(null);
		
		JLabel lblGameName = new JLabel("Dragons & Fireballs Leaderboard");
		lblGameName.setBounds(35, 6, 681, 82);
		lblGameName.setForeground(Color.WHITE);
		lblGameName.setFont(new Font("Lucida Grande", Font.PLAIN, 42));
		this.add(lblGameName);
		
		Object[] columns = new String[] {"Username", "Kills", "Deaths"};
		Object[][] data = new Object[30][3];
		
		data[0][0] = "Username";
		data[0][1] = "Kills";
		data[0][2] = "Deaths";
		
		Connection conn = null;
		Statement st = null;
		java.sql.PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/GameInfo?user=root&password=root&useSSL=false");
			st = conn.createStatement();
			rs = st.executeQuery("SELECT * FROM Users "
					+ "ORDER BY kills DESC;");
						
			int row = 1;
			
			while(rs.next()) {
				//add to jTable
				data[row][0] = rs.getString("username");
				data[row][1] = rs.getInt("kills");
				data[row][2] = rs.getInt("deaths");
				row++;
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
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setBounds(35, 97, 521, 463);
		
		this.add(scrollPane);
		
		leaderboard = new JTable(data, columns);
		leaderboard.setShowVerticalLines(false);
		leaderboard.setShowHorizontalLines(false);
		leaderboard.setShowGrid(false);
		leaderboard.setRowSelectionAllowed(false);
		leaderboard.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		leaderboard.setForeground(Color.BLACK);
		leaderboard.setBounds(35, 93, 463, 450);

		scrollPane.add(leaderboard);

		
	}
	
	private class SignOutAction extends AbstractAction 
	{
		private static final long serialVersionUID = 1L;
		public SignOutAction() 
		{
			putValue(NAME, "Sign Out");
			putValue(SHORT_DESCRIPTION, "Sign out and save your info.");
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			boolean success = authenticate();	
		}
		
		public boolean authenticate() {
			return true;
		}
		
	}

	private class JoinAction extends AbstractAction 
	{
		private static final long serialVersionUID = 1L;
		public JoinAction() 
		{
			putValue(NAME, "Join");
			putValue(SHORT_DESCRIPTION, "Join the game.");
		}
		public void actionPerformed(ActionEvent e) 
		{
			boolean success = authenticate();
		}
		
		public boolean authenticate() {
			return true;
		}
		
	}
}
