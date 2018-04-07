package client;

import java.awt.EventQueue;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import frames.Board;
import frames.MainFrame;
import serializedMessages.GameMessage;
import server.Movement;

public class Client extends Thread
{
	public int clientID;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private MainFrame ex;
	private Socket s;
	private Board board;
	public GameMessage gm = null;
	public boolean running = true;
	
	public Map<Integer, Movement> playerMap;
	
	public Client()
	{
		boolean connected = false;
		s = new Socket();
		EventQueue.invokeLater(() -> 
		{
			ex = new MainFrame(this.s);
	        ex.setVisible(true);    
	    });
		
		while(!connected)
		{
			try 
			{
				if(s.getInetAddress().isReachable(10000))
				{
					System.out.println("Connected! Socket Address: " + s.getInetAddress());
					connected = true;
					ois = new ObjectInputStream(s.getInputStream());
					oos = new ObjectOutputStream(s.getOutputStream());
					
					//Remove connection Panel and add game board panel
					//This initializes the game board.
			        ex.getContentPane().removeAll();
			        board = new Board(this.s, oos, this);
			        
			        try 
			        {
						gm = (GameMessage)ois.readObject();
						if(gm.getProtocol().equalsIgnoreCase("assignid"))
						{
							this.clientID = Integer.parseInt(gm.getMessage());
							System.out.println("Assigned Client ID: " + this.clientID);
						}
						
						gm = (GameMessage)ois.readObject(); 	 
						if(gm.getProtocol().equalsIgnoreCase("addedPlayer"))
						{
							playerMap = gm.playerMap;
							board.repaint();	
						}

					} 
			        catch (ClassNotFoundException e1) 
			        {
						e1.printStackTrace();
					}
			        ex.setContentPane(board);
			        ex.setLocationRelativeTo(null);
			        ex.setResizable(false);
			        ex.getContentPane().revalidate();
			        ex.repaint();
			        ex.requestFocusInWindow();
			        ex.addKeyListener(board.getKeyListeners()[0]);
			        KeyListener[] e = ex.getKeyListeners();
			        ex.setName("Game Board");
			        System.out.println("Started Client thread");
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

		//Add board Key listener right here 
		//while(true)
		//{}
	}
	
	public void run()
	{
		while(true)
		{
			try 
			{
				GameMessage gm = (GameMessage)ois.readObject();
				//System.out.println("Message Received in Client : " + gm.getProtocol());
				
				if(gm.getProtocol().equalsIgnoreCase("movementupdate"))
				{
					/*System.out.println("movement update on client: MSG: " + gm.getMessage());
					for(Map.Entry<Integer,Movement> entry : gm.playerMap.entrySet())
					{
						System.out.println("values in client before overwrite " + entry.getValue().clientID + "  " 
						+ entry.getValue().x + "  " + entry.getValue().y);	
					}
					 */
					playerMap = gm.playerMap;
					/*for(Map.Entry<Integer,Movement> entry : playerMap.entrySet())
					{	
						System.out.println("values in client after overwrite " + entry.getValue().clientID + "  " 
						+ entry.getValue().x + "  " + entry.getValue().y);	
					}
					*/
					board.repaint();
				}
			} catch (ClassNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
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

