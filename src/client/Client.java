package client;

import java.awt.EventQueue;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JPanel;
import frames.Board;
import frames.MainFrame;
import serializedMessages.GameMessage;
import serializedMessages.MovementMessage;
import sprites.Player;

public class Client extends Thread
{
	private int clientID;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private MainFrame ex;
	private Socket s;
	private JPanel board;
	GameMessage gm = null;
	public boolean running = true;
	
	
	
	public Client()
	{
		
		//InputStreamReader isr = new InputStreamReader(System.in);
		//BufferedReader br = new BufferedReader(isr);
		boolean connected = false;
		s = new Socket();
		EventQueue.invokeLater(() -> 
		{
			ex = new MainFrame(this.s);
	        ex.setVisible(true);    
	    });
		
		while(!connected)
		{
			try {
				if(s.getInetAddress().isReachable(10000))
				{
					System.out.println("Connected! Socket Address: " + s.getInetAddress());
					connected = true;
					ois = new ObjectInputStream(s.getInputStream());
					oos = new ObjectOutputStream(s.getOutputStream());
					//Remove connection Panel and add game board panel
			        ex.getContentPane().removeAll();
			        board = new Board(this.s, ois, oos, this);
			        ex.setContentPane(board);
			        ex.setLocationRelativeTo(null);
			        ex.setResizable(false);
			        ex.getContentPane().revalidate();
			        ex.repaint();
			        ex.requestFocusInWindow();
			        ex.addKeyListener(board.getKeyListeners()[0]);
			        ex.setName("Game Board");
				
			        //Start client thread run
			        this.start();
				}
			} 
			catch(SocketException se)
			{
				System.out.println("From Client: " + se );
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			catch(NullPointerException npe)
			{
				//Do nothing
			}
		}
		
		/* Might not need to do anything here.*/
		//while(true)
		//{	
		/* Might not need to do anything here.
			try 
			{
				oos.writeObject(gm);
				oos.flush();
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		*/
		//}	
	}
	
	public void run()
	{
		try 
		{
			while(true) 
			{
				GameMessage gm = (GameMessage)ois.readObject();
				
				//Assign ID
				System.out.println("Message Received: " + gm.getMessage());
				
				if(gm.getProtocol().equalsIgnoreCase("assignid"))
				{
					this.clientID = Integer.parseInt(gm.getMessage());
					System.out.println("Assigned Client ID: " + this.clientID);
				}
				if(gm.getProtocol().equalsIgnoreCase("movementupdate"))
				{
					//For through whole map. Repaint all characters in the map.
					//board.repaint();
					
					
					
					for(Map.Entry<Integer,Player> entry : gm.playerMap.entrySet())
					{
						int currentID = entry.getKey();
						board.player = entry.getValue();
						repaint(board.player.getX()-1, board.player.getY()-1,
								board.player.getWidth()+2, board.player.getHeight()+2);
					}
					
					
				}

			}
		}
		catch (IOException ioe) 
		{
			System.out.println("ioe in ChatClient.run(): " + ioe.getMessage());
		} 
		catch (ClassNotFoundException cnfe) 
		{
			System.out.println("cnfe: " + cnfe.getMessage());
		}
	}
	
	public int getID()
	{
		return this.clientID;
	}
	
	public static void main(String [] args) throws InvocationTargetException, InterruptedException
	{
		Client gc = new Client();
	}
}

