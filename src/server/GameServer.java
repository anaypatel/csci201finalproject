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
import java.util.HashMap;
import java.util.Map;

import serializedMessages.GameMessage;
import sprites.Player;

public class GameServer  extends Thread
{
	private int clientIDCounter = 0;
	
	//private Map<Integer, Movement> playerMap;
	private Map<Integer, Player> playerMap;
	
	private MulticastSocket socket;
	private GameMessage gm;
	public GameServer(int port)
	{
		//playerMap = new HashMap<Integer, Movement>();
		playerMap = new HashMap<Integer, Player>();
		try 
		{
			System.out.println("Binding to port: " + port);
			socket = new MulticastSocket(port);
			System.out.println("Bound to port " + port);
			this.start();
		} catch (IOException ioe) {
			System.out.println("Error From GameRoom Constructor: " + ioe);
		
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
		InetAddress address = null;
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
			data = new byte[1024];
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
				
				//playerMap.get(gm.player.getID()).missiles = temp.missiles;
				
				System.out.println("GM Player Missile Size: " + gm.player.missiles.size());
				
				temp.missiles = gm.player.missiles;
				///playerMap.get(gm.getID()).setX(gm.player.getX());
				//playerMap.get(gm.getID()).setY(gm.player.getY());
				
				
				//playerMap.get(gm.player.getID()) = gm.player.getY();
				
				gm = new GameMessage(gm.getID(), "movementupdate", "");
				
				//System.out.println("Movement");
				gm.playerMap = playerMap;
				
				System.out.println("Player Missile size: " + temp.missiles.size());
				
				data = serializeGM(baos, gm, oos);
				
				try 
				{
					packet = new DatagramPacket(data, data.length, address, 4000);
					socket.send(packet);
				} 
				catch (UnknownHostException e) 
				{
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(gm.getProtocol().equals("assignid"))
			{	
				System.out.println("Assigned Client ID : " 
						           + clientIDCounter + " For port : " + packet.getPort());
				GameMessage assignID = new GameMessage(clientIDCounter, "assignedid", "" 
						               + packet.getPort());	
				Player player = new Player(clientIDCounter, 300, 300, "");
				playerMap.put(clientIDCounter, player);
				gm.playerMap = playerMap;
				gm.player = player;
				data = serializeGM(baos, assignID, oos);
				sendData(data, packet.getAddress(), packet.getPort());					
				++clientIDCounter;
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
		byte [] data = new byte [1024];
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
