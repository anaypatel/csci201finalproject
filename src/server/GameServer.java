package server;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import serializedMessages.GameMessage;
import sprites.Player;
import sprites.Projectile;

public class GameServer  extends Thread
{
	private int clientIDCounter = 0;
	private Map<Integer, Player> playerMap;
	private MulticastSocket socket;
	private GameMessage gm = new GameMessage(-1, "" , "");
	private boolean gameRunning;
	public GameServer(int port)
	{
		//Set Game Running condition to false & initialize PlayerMap
		gameRunning = false;
		playerMap = Collections.synchronizedMap(new HashMap<Integer, Player>());

		//Try Binding to server
		try 
		{
			System.out.println("Binding to port: " + port);
			socket = new MulticastSocket(port);
			System.out.println("Bound to port " + port);
			this.start();
		} catch (IOException ioe) {
			System.out.println("Error From GameRoom Constructor: " + ioe);
		
		}
		
		//Initialize Game Loop
		initGameLoop();		
	}

	private void initGameLoop()
	{
		//Data members for sending/rec packets
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		DatagramPacket packet;
		InetAddress address = null;
		byte[] data;
		
		//Set Multicast Broadcast IP
		try 
		{
			address = InetAddress.getByName("224.0.0.1");
		} 
		catch (UnknownHostException e1) 
		{
			e1.printStackTrace();
		}
		
		//Main Thread for Collision of Projectiles and Players
		//Main Thread is also for sending periodic updates of client updates to 
		//all clients
		while(true)
		{	
			//If players are added start analysis of game
			//Will update to players >= 4 or 8
			if(playerMap.size() >= 2)
			{
				//Create Collision map of Client ID Keys and Player Sprite Bounds
				Map<Integer,Rectangle> collisionChecker = new HashMap<Integer,Rectangle>();
				
				//Add player bounds to collision checker data structure
				for(Map.Entry<Integer,Player> entry : playerMap.entrySet())
				{
					if(entry.getValue().health > 0)
					{
						collisionChecker.put(entry.getKey(),entry.getValue().getBounds());
					}
				}
				
				//If last man standing then he wins!
				/*
					if(collisionChecker.size() == 1)
					{
						//Get collision checker only guy left and give him win point
					}
				*/
				//Iterate through Player Map and move/remove missiles
				for(Map.Entry<Integer,Player> entry : playerMap.entrySet())
				{
					if(entry.getValue().missiles.size() > 0)
					{
						for(int j = 0; j < entry.getValue().missiles.size(); ++j)
						{		
							//Check each projectile and player bounds for intersections. If intersection found
							//Remove missile and deduct health
							for(Map.Entry<Integer, Rectangle> collisionEntry : collisionChecker.entrySet())
							{
								//If collision occured update stats
								if(collisionEntry.getValue().intersects(entry.getValue().missiles.get(j).getBounds())
										&& (collisionEntry.getKey() != entry.getKey()))
								{
									//If health > 0
									if(playerMap.get(collisionEntry.getKey()).health > 0)
									{
										entry.getValue().missiles.get(j).setVisible(false);
										entry.getValue().hits +=1;
										playerMap.get(collisionEntry.getKey()).health -= 1;
										
										if(playerMap.get(collisionEntry.getKey()).health == 0)
										{
											playerMap.get(collisionEntry.getKey()).deaths++;
											entry.getValue().kills += 1;
										}										
										gameRunning = true;
									}
								}
							}
							//Remove missiles if not visible or move them if they are
				            if (entry.getValue().missiles.get(j).isVisible()) 
				            {
				            	entry.getValue().missiles.get(j).move();
				            	gameRunning = true;
				            }
				            else
				            {
				            	entry.getValue().missiles.remove(j);
				            }
				        }
					}
				}
			}
			//If a condition changed on a client send update to all clients
			if(gameRunning)
			{
				gameRunning = false;
				gm = new GameMessage(-1, "movementupdate", "GameLoop Message");
				gm.playerMap = playerMap;
				data = serializeGM(baos, gm, oos);
				
				try 
				{
					packet = new DatagramPacket(data, data.length, address, 4000);
					socket.send(packet);
				} 
				catch (UnknownHostException e) 
				{
					e.printStackTrace();
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
			}	
			//Make thread sleep every 15 ms 
			try
			{
				Thread.sleep(15,0);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}	
		}	
	}
	
	public void run()
	{
		//Packet Data Members
		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		DatagramPacket packet;
		byte[] data;

		//Server Incoming Client Conditions loop
		//Receives Packets about player movement, projectiles, and
		//requests for client ID
		while(true)
		{
			data = new byte[4024];
			packet = new DatagramPacket(data, data.length);
			try 
			{
				socket.receive(packet);	
				gm = deSearializeGM(data, bais, ois);			
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			
			if(gm.getProtocol().equals("movement"))
			{
				Player temp = playerMap.get(gm.player.getID());
				temp.setX(gm.player.getX());
				temp.setY(gm.player.getY());
				temp.direction = gm.player.direction;
				gameRunning = true;
			}
			else if(gm.getProtocol().equals("projectile"))
			{
				Player temp = playerMap.get(gm.player.getID());
				temp.setX(gm.player.getX());
				temp.setY(gm.player.getY());
				temp.missiles.add(new Projectile(temp.getX(), temp.getY(),
							      temp.getID(), gm.player.direction));
				gameRunning = true;
				
			}
			else if(gm.getProtocol().equals("assignid"))
			{	
				System.out.println("Assigned Client ID : " 
						           + clientIDCounter 
						           + " For port : " 
						           + packet.getPort());
				GameMessage assignID = new GameMessage(clientIDCounter, 
								       "assignedID", "" + packet.getPort());	
				
				//Algorithm for choosing player colors off of sprite
				if(clientIDCounter  % 8 >=0 && clientIDCounter  % 8 < 4)
				{
					gm.player.playerColorX = (clientIDCounter  % 8) * 144;  
					gm.player.playerColorY = 0;
				}
				else
				{
					gm.player.playerColorX = (clientIDCounter  % 4) * 145; 
					gm.player.playerColorY = 192;
				}
				
				gm.player.setClientID(clientIDCounter);
				gm.player.setX(600);
				gm.player.setY(400);
				playerMap.put(clientIDCounter, gm.player);
				assignID.playerMap = playerMap;
				assignID.player = gm.player;
				data = serializeGM(baos, assignID, oos);
				sendData(data, packet.getAddress(), packet.getPort());					
				++clientIDCounter;
				gameRunning = true;
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
		byte [] data = new byte [4024];
		baos = new ByteArrayOutputStream();
		try {
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
	
	public void sendData(byte[] data, InetAddress ipAddress, int port)
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try 
		{
			socket.send(packet);	
		} 
		catch (IOException e) 
		{		
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		try 
		{
			System.out.print("Please enter a valid port: ");
			int port = Integer.parseInt(br.readLine());

			while (port < 1024) 
			{
				System.out.println("Invalid Port!");
				System.out.print("Please enter a valid port: ");
				port = Integer.parseInt(br.readLine());
			}
			
			System.out.println("Success!");
			@SuppressWarnings("unused")
			GameServer gr = new GameServer(port);

		} catch (IOException ioe) 
		{
			System.out.println(ioe.getMessage());
		}
	}
}
