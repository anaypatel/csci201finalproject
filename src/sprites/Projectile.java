package sprites;

import java.io.Serializable;

public class Projectile implements Serializable
{
	private int x, y;
	private int clientID;
	
	private final int BOARD_WIDTH = 720;
    private final int MISSILE_SPEED =3;
	private String direction;
	private boolean visible;
	
	public Projectile(int x, int y, int id, String direction)
	{
		this.x = x;
		this.y = y;
		this.clientID = id;
	}

	 public void move()
	 {
        
        x += MISSILE_SPEED;
        
        if (x > BOARD_WIDTH) 
        {
            visible = false;
        }
	 }
	 
		public int getX()
		{
			return this.x;
		}
		public int getY()
		{
			return this.y;
		}
		
		public boolean isVisible()
		{
			return visible;
		}

}
