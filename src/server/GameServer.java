package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import playerActions.Movement;
import serializedMessages.MovementMessage;

public class GameServer 
{
	private ArrayList<ServerThread> serverThreads;
	private static int threadIDCounter = 0;
	private ArrayList<Movement> movementTracker;
	
	public GameServer(int port)
	{
		try {
			System.out.println("Binding to port: " + port);
			ServerSocket ss = new ServerSocket(port);
			System.out.println("Bound to port " + port);
			serverThreads = new ArrayList<ServerThread>();
			while (true) {
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
	
	public void broadcast(MovementMessage mmsg, ServerThread st) 
	{
		if(mmsg != null)
		{
			System.out.println("X: " + mmsg.getX() + "  Y: " + mmsg.getY()); 
		}
	}
	
}
