package client;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.image.BufferedImage;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
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
	public BufferedImage background;
	public BufferedImage playerSheet, projectileSheet;
	public Image dead;
	
	public Client()
	{
		//Initialize Hashmap
		playerMap = Collections.synchronizedMap(new HashMap<Integer, Player>());
		boolean connected = false;
		
		//Load images for drawing
		dead = loadImage("bones");
		try 
		{
			projectileSheet = ImageIO.read(getClass().getResource("/resources/projectile.png"));
			playerSheet = ImageIO.read(getClass().getResource("/resources/player.png"));
			background = ImageIO.read(getClass().getResource("/resources/background.png"));	
		} 
		catch (IOException e3) 
		{
			e3.printStackTrace();
		}
		
		//Create UDP Socket
		try 
		{
			socket = new MulticastSocket();
		} 
		catch (IOException e2) 
		{
			e2.printStackTrace();
		}
		
		//Initialize GUI
		EventQueue.invokeLater(() -> 
		{
			ex = new MainFrame(this.socket);
			 ex.setLocationRelativeTo(null);
	        ex.setVisible(true);    
	    });
		
		//Connect to server and get Client ID and get added to server player Map
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
			        ex.setSize(1680, 900);
			        ex.setFocusable(true);;
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
		
		byte[] data = new byte[4024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		
		//Obtain Client ID
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
		        	&& gm.getMessage().trim().equalsIgnoreCase(String.valueOf(socket.getLocalPort())))
		        {
		        	System.out.println("Client ID Assigned: " + gm.getID() 
		        	+ " Starting Coordinate (" + gm.player.getX()
		        	+ "," +gm.player.getY() +")");
		        	
		        	this.clientID = gm.getID();		 
		        	playerMap = gm.playerMap;
		        	board.player.setX(gm.player.getX());
		        	board.player.setY(gm.player.getY());
		        	board.player.setClientID(this.clientID);
		        }
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
		}
		   this.start();
		   
		   /* Needs to be implemented to ensure server that client is still joined.
		    *Also need to implement a check for last player who stands wins.
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

	//Send update of player Coordinates and projectile fire
	public void sendPlayerUpdate(String type)
	{
		byte[] data = new byte[4024];
		
		data = new byte[4024];

		if(type.equalsIgnoreCase("movement"))
		{
			GameMessage gm = new GameMessage(clientID, "movement", "");
			gm.player = board.player;
			data = serializeGM(baos, gm, oos);
			sendData(data);
			
		}
		else if(type.equalsIgnoreCase("projectile"))
		{
			GameMessage gm = new GameMessage(clientID, "projectile", "");
			gm.player = board.player;
			data = serializeGM(baos, gm, oos);
			sendData(data);
		}	
	}
	
	//Function for loading Image
	public Image loadImage(String name)
	{
		ImageIcon ic = new ImageIcon("src/resources/" + name + ".png");
		Image image = ic.getImage();
		return image;
	}
	
	//Client Thread
	public synchronized  void run()
	{
		//Join Multi-Cast Group on Port 4000 at BroadCast IP 224.0.0.1
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

		//Retrieve Multi-Cast Packets for server updates
		while(true)
		{
			 data = new byte[4024];
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
				board.player.health = playerMap.get(clientID).health;
				board.repaint();	
			}
		}
	}
	
	//DeSearialize Game Messsages
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

	//Searialize Game Messages
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

	//Send Data packet through UDP Socket "socket"
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
	
	//Return Assigned Client ID
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

