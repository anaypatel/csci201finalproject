package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import serializedMessages.MovementMessage;

public class ServerThread extends Thread
{
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private int id;
	
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
	
	public void sendMessage(MovementMessage mmsg)
	{
		try
		{
			oos.writeObject(mmsg);
			oos.flush();
		}
		catch(IOException ioe)
		{
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	public void run() 
	{
		System.out.println("Ran Thread From Server Thread Class");
		try 
		{
			//GameMessage assignID = new GameMessage(this.getSID(), "assignid", "Assigning new ID to Client");
			//gr.broadcast(assignID, this);
			//System.out.println("broadcasted assign ID from sereverThread!");
			//GameMessage assignRoom = new GameMessage(this.getSID(), "assignroom", "Please make a choice:\n1)Start Game \n2)Join Game");
			//gr.broadcast(assignRoom, this);

			//Receive messages from Client after a flush has happened, then broadcast it. 
			while(true) 
			{
				System.out.println("in ServerThread While true!");
				MovementMessage mmsg = (MovementMessage)ois.readObject();
				server.broadcast(mmsg, this);
			}
		} catch (IOException ioe) 
		{
			System.out.println("ioe in ServerThread.run(): " + ioe.getMessage());
		} catch (ClassNotFoundException cnfe) 
		{
			System.out.println("cnfe: " + cnfe.getMessage());
		}
	}
}
