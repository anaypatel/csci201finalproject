package serializedMessages;

import java.io.Serializable;
import java.util.Map;

import server.Movement;
import sprites.Player;

public class GameMessage  implements Serializable 
{
	public static final long serialVersionUID = 1;
	private int x, y;
	private int clientID;
	private String protocol;
	private String message;
	public Player player;
	public Map<Integer, Movement> playerMap;
	public Movement m;
	
	public GameMessage(int clientID, String protocol, String message)
	{
		this.clientID= clientID;
		this.protocol = protocol;
		this.message = message;
	}
	
	//Constructor for movement
	public GameMessage(int clientID, String protocol, int x, int y)
	{
		this.clientID = clientID;
		this.protocol = protocol;
		this.x = x;
		this.y = y;
	}
	
	public int getX()
	{
		return this.x;
	}
	public int getY()
	{
		return this.y;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public String getProtocol()
	{
		return this.protocol;
	}
	
	public int getID()
	{
		return clientID;
	}
}
