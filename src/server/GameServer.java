package server;

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
	private boolean gameRunning, ready;
	public GameServer(int port)
	{
		gameRunning = false;
		playerMap = Collections.synchronizedMap(new HashMap<Integer, Player>());

		
		try 
		{
			System.out.println("Binding to port: " + port);
			socket = new MulticastSocket(port);
			System.out.println("Bound to port " + port);
			this.start();
		} catch (IOException ioe) {
			System.out.println("Error From GameRoom Constructor: " + ioe);
		
		}

		initGameLoop();		
	}

	private void initGameLoop()
	{
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		DatagramPacket packet;
		InetAddress address = null;
		byte[] data;
		
		try 
		{
			address = InetAddress.getByName("224.0.0.1");
		} 
		catch (UnknownHostException e1) 
		{
			e1.printStackTrace();
		}
		
		while(true)
		{
			/*
			System.out.println("Player Map Iteration");
			
			for(int i = 0; i < playerMap.size(); ++i)
			{
				
				
				System.out.println(playerMap.get(i).getID() + " | x: " + playerMap.get(i).getX()
						+" y: " + playerMap.get(i).getY());
			}
			
			*/
			//if(ready)
			//{
				//System.out.println("Ready");
			/*
			Map<Integer, Player> collisionMap = new HashMap<Integer, Player>();
			
			for(int i = 0; i < playerMap.size(); ++i)
			{
				Player temp = playerMap.get(i);
				if(collisionMap.containsKey(temp.getX()))
				{
					Player possibleCollider = collisionMap.get(temp.getX());
					if(temp.getY() == possibleCollider.getY())
					{
						System.out.println("Two players Collided!");
					}
				}
				else
				{
					collisionMap.put(temp.getX(), temp);
				}
				
			}
			for(int i = 0; i < playerMap.size(); ++i)
			{
				if(playerMap.get(i).missiles.size() > 0)
				{
					for(int j = 0; j < playerMap.get(i).missiles.size(); ++j)
					{		
						
			            if (playerMap.get(i).missiles.get(j).isVisible()) 
			            {
			            	if((collisionMap.containsKey(playerMap.get(i).missiles.get(j).getX())) &&
			            	   (playerMap.get(i).getID() !=  
			            		collisionMap.get(playerMap.get(i).missiles.get(j).getX()).getID()))
			            	{
			            		if(collisionMap.get(playerMap.get(i).missiles.get(j).getX()).getY() ==
			            				playerMap.get(i).missiles.get(j).getY())
			            		{
			            			System.out.println("Collision with projectile detected");
			            			playerMap.get(i).missiles.get(j).setVisible(false);
			            		}
			            		else
			            		{
			            			playerMap.get(i).missiles.get(j).move();
			            		}
			            	}
			            	else
		            		{
		            			playerMap.get(i).missiles.get(j).move();
		            		}
			            	System.out.println("Main game true from main thread");
			            	gameRunning = true;
						}
			            else 
			            {
			            	playerMap.get(i).missiles.remove(j);
			            }
					}	
				}
				
			}
			
			*/
			//}
			
			if(playerMap.size() > 0)
			{
				
				for(int i = 0; i < playerMap.size(); ++i)
				{
					if(playerMap.get(i).missiles.size() > 0)
					{
						for(int j = 0; j < playerMap.get(i).missiles.size(); ++j)
						{		
							
				            if (playerMap.get(i).missiles.get(j).isVisible()) 
				            {
				            	playerMap.get(i).missiles.get(j).move();
				            	gameRunning = true;
				            }
				            else
				            {
				            	playerMap.get(i).missiles.remove(j);
				            }
				        }
					}
				}
			
			}
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
			
			
			
			try
			{
				Thread.sleep(9,0);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			
		}
		
	}
	

	public void run()
	{
		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		DatagramPacket packet;
		byte[] data;

		while(true)
		{
			data = new byte[4024];
			packet = new DatagramPacket(data, data.length);
			try 
			{
				socket.receive(packet);	
				//
			//	ready = false;
				gm = deSearializeGM(data, bais, ois);		
				
				//System.out.println(gm.getProtocol());
			
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			
			if(gm.getProtocol().equals("movement"))
			{
				/*
				System.out.println("GM Protocol: " 
									+ gm.getProtocol() 
									+ " ClientID: " 
									+ gm.getID()
									+"\n Coordinates Received: X: " 
									+ gm.player.getX() + " Y: " 
									+ gm.player.getY() );
				
				System.out.println("BEFORE Player Map Iteration");
				
				for(int i = 0; i < playerMap.size(); ++i)
				{
					
					
					System.out.println(playerMap.get(i).getID() + " | x: " + playerMap.get(i).getX()
							+" y: " + playerMap.get(i).getY());
				}
				*/
					//Player temp = playerMap.get(gm.player.getID());
					//temp.setX(gm.player.getX());
					//temp.setY(gm.player.getY());
				
					
					//playerMap.get(gm.player.getID()).setX(gm.player.getX());
					//playerMap.get(gm.player.getID()).setY(gm.player.getY());
					//playerMap.get(gm.player.getID()).missiles = gm.player.getMissiles();
					
					for(Map.Entry<Integer,Player> entry : playerMap.entrySet())
					{
						if(gm.getID() == entry.getKey())
						{
							//System.out.println("Key Matched: " + gm.getID());
							entry.getValue().setX(gm.player.getX());
							entry.getValue().setY(gm.player.getY());
							entry.getValue().direction = gm.player.direction;
							
						}
					}
				
				
					/*
					System.out.println("AFTER Player Map Iteration");
					
					for(int i = 0; i < playerMap.size(); ++i)
					{
						
						
						System.out.println(playerMap.get(i).getID() + " | x: " + playerMap.get(i).getX()
								+" y: " + playerMap.get(i).getY());
					}
				*/
					//playerMap.get(gm.player.getID()).setX(gm.player.getX());
					//playerMap.get(gm.player.getID()).setY(gm.player.getY());
					//playerMap.get(gm.player.getID()).missiles = gm.player.getMissiles();
					gameRunning = true;
			}
			else if(gm.getProtocol().equals("projectile"))
			{
				
				/*
				System.out.println("GM Protocol: " 
					+ gm.getProtocol() 
					+ " ClientID: " 
					+ gm.getID()
					+"\n Coordinates Received: X: " 
					+ gm.player.getX() + " Y: " 
					+ gm.player.getY() );
				
				*/
				
				//Player temp = playerMap.get(gm.player.getID());
				//temp.setX(gm.player.getX());
				//temp.setY(gm.player.getY());
				//temp.missiles.add(new Projectile(temp.getX(), temp.getY(),temp.getID(), temp.direction));
				
				
				for(Map.Entry<Integer,Player> entry : playerMap.entrySet())
				{
					if(gm.getID() == entry.getKey())
					{
						//System.out.println("Key Matched: " + gm.getID());
						entry.getValue().missiles.add(new Projectile(entry.getValue().getX(), 
								entry.getValue().getY(),
								entry.getValue().getID(), 
								entry.getValue().direction));
					}
				}
				
				
				
				//playerMap.get(gm.player.getID()).setX(gm.player.getX());
				//playerMap.get(gm.player.getID()).setY(gm.player.getY());
				//playerMap.get(gm.player.getID()).missiles.add(new Projectile(x, y,clientID, direction))
				
				
				
				gameRunning = true;
				
			}
			else if(gm.getProtocol().equals("assignid"))
			{	
				System.out.println("Assigned Client ID : " 
						           + clientIDCounter + " For port : " + packet.getPort());
				GameMessage assignID = new GameMessage(clientIDCounter, "assignedID", "" 
						               + packet.getPort());	
				
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
			
			//ready = true;
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
