package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import playerActions.Movement;
import serializedMessages.GameMessage;
import serializedMessages.MovementMessage;
import sprites.Player;

public class GameServer 
{
	private ArrayList<ServerThread> serverThreads;
	private static int threadIDCounter = 0;
	private Map<Integer, Player> playerMap;
	//private ArrayList<Movement> movementTracker;
	//private ArrayList<Player> playerList;
	
	public GameServer(int port)
	{
		playerMap = new HashMap<Integer, Player>();
		try 
		{
			System.out.println("Binding to port: " + port);
			ServerSocket ss = new ServerSocket(port);
			System.out.println("Bound to port " + port);
			serverThreads = new ArrayList<ServerThread>();
			while (true) 
			{
				Socket s = ss.accept();
				System.out.println("Connection from: " + s.getInetAddress());
				ServerThread st = new ServerThread(s, this, threadIDCounter);
				++threadIDCounter;
				serverThreads.add(st);
			}
		} catch (IOException ioe) {
			System.out.println("Error From GameRoom Constructor: " + ioe);
		}
	}
	
	public void broadcast(GameMessage gm, ServerThread st) 
	{
		if(gm != null)
		{
			///For testing purposes to see if clients are sending server data!
			 System.out.println("Message has been broadcasted");
			
			if(gm.getProtocol().equals("movement"))
			{
				System.out.println("Movement from :" + gm.getID() + " X: " + gm.getX() + "  Y: " + gm.getY());
			}
			if(gm.getProtocol().equals("assignid"))
			{
				System.out.println("Assigned Client ID : " + gm.getID());
			}
			if(gm.getProtocol().equals("addplayer")){
				playerMap.put(gm.getID(), gm.player);
				System.out.println("Added player to map");
				//playerMap.get(gm.getID().move etc etc) possibly alter player position for testing
				GameMessage message = new GameMessage(st.getSID(), "addedplayer", "hi");
				message.playerMap = playerMap;
				st.sendMessage(message);
				System.out.println("Sent updated player map to player: " + gm.getID());
			}
			
			//st.sendMessage(gm);
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

			while (port < 1024) {
				System.out.println("Invalid Port!");
				System.out.print("Please enter a valid port: ");
				port = Integer.parseInt(br.readLine());
			}

			System.out.println("Success!");

			GameServer gr = new GameServer(port);

		} catch (IOException ioe) 
		{
			System.out.println(ioe.getMessage());
		}
	}
	
}
