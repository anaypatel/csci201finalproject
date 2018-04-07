package server;

import java.io.Serializable;

public class Movement implements Serializable
{
	private static final long serialVersionUID = 1L;
	public int clientID;
	public int x;
	public int y;

	Movement(int clientID, int x, int y)
	{
		this.clientID = clientID;
		this.x = x;
		this.y = y;
	}
}
