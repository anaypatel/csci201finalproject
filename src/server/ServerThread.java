package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import serializedMessages.GameMessage;
import serializedMessages.MovementMessage;

public class ServerThread extends Thread
{
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private int id;
	private boolean inGame = false;
	
	private GameServer server;
	public ServerThread(Socket s, GameServer _server, int _id)
	{
		try
		{
			this.id = _id;
			this.server = _server;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
			
		}
		catch(IOException ioe)
		{
			//Meow
		}
	}
	
	public void sendMessage(GameMessage gm)
	{
		try
		{
			oos.writeObject(gm);
			oos.flush();
		}
		catch(IOException ioe)
		{
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	public int getSID()
	{
		return this.id;
	}
	
	public void run() 
	{
		try 
		{
			GameMessage assignID = new GameMessage(this.getSID(), "assignid", String.valueOf(this.getSID()));
			server.broadcast(assignID, this);
			
			//Receive messages from Client after a flush has happened, then broadcast it. 
		
			while(true) 
			{
				GameMessage gm = (GameMessage)ois.readObject();
				server.broadcast(gm, this);
			}
		} 
		catch (IOException ioe) 
		{
			System.out.println("ioe in ServerThread.run(): " + ioe.getMessage());
		} 
		catch (ClassNotFoundException cnfe) 
		{
			System.out.println("cnfe: " + cnfe.getMessage());
		}
	}
}
