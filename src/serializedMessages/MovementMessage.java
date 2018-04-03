package serializedMessages;

import java.io.Serializable;

public class MovementMessage  implements Serializable 
{
	public static final long serialVersionUID = 1;
	private int x, y;
	private String protocol;
	private String message;
	
	public MovementMessage(int x, int y)
	{
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
}
