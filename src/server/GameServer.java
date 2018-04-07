package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import server.Movement;
import serializedMessages.GameMessage;

public class GameServer 
{
	private ArrayList<ServerThread> serverThreads;
	private static int threadIDCounter = 0;
	private Map<Integer, Movement> playerMap;
	
	public GameServer(int port)
	{
		playerMap = new HashMap<Integer, Movement>();
		try 
		{
			System.out.println("Binding to port: " + port);
			@SuppressWarnings("resource")
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

			if(gm.getProtocol().equals("movement"))
			{
				playerMap.get(gm.getID()).x = gm.getX();
				playerMap.get(gm.getID()).y = gm.getY();
				gm = new GameMessage(gm.getID(), "movementupdate", "");
				gm.playerMap = playerMap;

				for(ServerThread st1 : serverThreads)
				{
					//System.out.println("Sent Message to " + st1.getSID());
					st1.sendMessage(gm);
				}
			}
			else if(gm.getProtocol().equalsIgnoreCase("addplayer"))
			{
				//System.out.println("Entered Added Player Server");
				Movement m = new Movement(gm.getID(), 300,500);
				playerMap.put(st.getSID(), m);
				gm = new GameMessage(gm.getID(), "addedplayer", "");
				gm.playerMap = playerMap;
				st.sendMessage(gm);
			}
			else if(gm.getProtocol().equals("assignid"))
			{
				//System.out.println("Assigned Client ID : " + gm.getID());
				st.sendMessage(gm);
			}
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

			@SuppressWarnings("unused")
			GameServer gr = new GameServer(port);

		} catch (IOException ioe) 
		{
			System.out.println(ioe.getMessage());
		}
	}
	
}
