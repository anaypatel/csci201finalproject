package serializedMessages;

import java.io.Serializable;
import java.util.Map;
import server.Movement;
import sprites.Player;

public class GameMessage  implements Serializable 
{
	public static final long serialVersionUID = 1;
	private int clientID;
	private String protocol;
	private String message;
	public Player player = new Player();
	public Map<Integer, Player> playerMap;
	public Movement m;
	
	public GameMessage(int clientID, String protocol, String message)
	{
		this.clientID= clientID;
		this.protocol = protocol;
		this.message = message;

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
