package client;

import java.awt.EventQueue;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import frames.Board;
import frames.MainFrame;
import serializedMessages.GameMessage;
import sprites.Player;

public class Client extends Thread
{
	public int clientID = -1;
	private MainFrame ex;
	public MulticastSocket socket;
	private Board board;
	public GameMessage gm = null;
	public boolean running = false;
	private MulticastSocket ms = null;
	public ByteArrayOutputStream baos = null;
	private ByteArrayInputStream bais = null;
	public ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	public Map<Integer, Player> playerMap;
	public Image playerSprite;
	public Image projectileSprite;
	public Image proj1, proj2;
	public Image background;
	
	public Client()
	{
		playerMap = new HashMap<Integer, Player>();
		boolean connected = false;
		playerSprite = loadImage("player");
		projectileSprite = loadImage("projectile");
		proj1 = loadImage("proj1");
		proj2 = loadImage("proj2");	
		background = loadImage("background");
		
		try 
		{
			socket = new MulticastSocket();
		} 
		catch (IOException e2) 
		{
			e2.printStackTrace();
		}
		
		EventQueue.invokeLater(() -> 
		{
			ex = new MainFrame(this.socket);
			 ex.setLocationRelativeTo(null);
	        ex.setVisible(true);    
	    });
		
		while(!connected)
		{
			try 
			{
				if(socket.getInetAddress().isReachable(10000))
				{
					System.out.println("Connected! Socket Address: " + socket.getInetAddress());
					connected = true;
			        ex.getContentPane().removeAll();
			        board = new Board(this.socket, oos, this);
			        ex.setContentPane(board);
			        ex.setLocationRelativeTo(null);
			        ex.setResizable(false);
			        ex.pack();
			        ex.setSize(1280, 720);
			        ex.getContentPane().revalidate();
			        ex.repaint();
			        ex.requestFocusInWindow();
			        ex.addKeyListener(board.getKeyListeners()[0]);
			        ex.setName("Game Board");
			     
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
		
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		
		while(this.clientID == -1) 
		{
			GameMessage assignID = new GameMessage(-1, "assignid", "" + this.getId());	
			assignID.player = board.player;
	        data = serializeGM(baos, assignID, oos);
			try 
			{
				sendData(data);
				data = new byte[2024];
				packet = new DatagramPacket(data, data.length);
		        socket.receive(packet);		        
		        gm = deSearializeGM(data, bais, ois);	 

		        if(gm.getProtocol().equalsIgnoreCase("assignedID") 
		        	&& gm.getMessage().trim().equalsIgnoreCase( String.valueOf(socket.getLocalPort())))
		        {
		        	this.clientID = gm.getID();		 
		        	playerMap = gm.playerMap;
		        	board.player.setX(gm.player.getX());
		        	board.player.setY(gm.player.getY());
		        }
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
		}
		   this.start();
		   

		   /* Needs to be implemented to ensure server that client is still joined.
		   while(true)
		   {
			   if(running)
			   {
				   	data = new byte[2024];
					packet = new DatagramPacket(data, data.length);
					GameMessage gm = new GameMessage(clientID, "connected", "");
					gm.player = board.player;
					data = serializeGM(baos, gm, oos);
					sendData(data);
					running = false;
			   }
			   
				try 
				{
					Thread.sleep(0, 0);
				} catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}	
			
			*/
	}
	
	
	public void sendPlayerUpdate()
	{
		byte[] data = new byte[2024];
		
		data = new byte[1024];
		
		GameMessage gm = new GameMessage(clientID, "movement", "");
		gm.player = board.player;
		data = serializeGM(baos, gm, oos);
		sendData(data);
	}
	
	public Image loadImage(String name)
	{
		System.out.println("Image loaded");
		ImageIcon ic = new ImageIcon("src/resources/" + name + ".png");
		Image image = ic.getImage();
		return image;
	}
	
	
	@SuppressWarnings("resource")
	public synchronized  void run()
	{
		try 
		{
			ms = new MulticastSocket(4000);
			InetAddress group = InetAddress.getByName("224.0.0.1");
			ms.joinGroup(group);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		byte[] data;
		DatagramPacket packet;

		while(true)
		{
			 data = new byte[2024];
			 packet = new DatagramPacket(data, data.length);
			try 
			{
				ms.receive(packet);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			gm = deSearializeGM(data, bais, ois);	
			if(gm.getProtocol().equalsIgnoreCase("movementupdate"))
			{
				playerMap = gm.playerMap;
				board.player.missiles = gm.playerMap.get(this.clientID).getMissiles();
				board.repaint();	
			}  
		}
	}
	

	public GameMessage deSearializeGM(byte[] data, ByteArrayInputStream bais, 
				  ObjectInputStream ois )
	{
		GameMessage gm1 = null;
		try 
		{
			bais = new ByteArrayInputStream(data);
			ois = new ObjectInputStream(bais);
			gm1 = (GameMessage)ois.readObject();
		} 
		catch (ClassNotFoundException | IOException e) 
		{
			e.printStackTrace();
		}	
		return gm1;
	}

	public byte[] serializeGM(ByteArrayOutputStream baos, GameMessage gm2, 
				 ObjectOutputStream oos)
	{
		byte [] data = null;
		baos = new ByteArrayOutputStream();
		try 
		{
			oos = new ObjectOutputStream(baos);
			oos.writeObject(gm2);
			oos.flush();
			data = baos.toByteArray();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		return data;
	}

	public void sendData(byte[] data)
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, socket.getInetAddress(), socket.getPort());
		try 
		{
			socket.send(packet);
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public int getID()
	{
		return this.clientID;
	}
	@SuppressWarnings("unused")
	public static void main(String [] args) throws InvocationTargetException, InterruptedException
	{
		Client gc = new Client();
	}
}

