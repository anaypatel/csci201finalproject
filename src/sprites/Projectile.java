package sprites;

import java.io.Serializable;

public class Projectile implements Serializable
{
	private int x, y;
	private int clientID;
	
	private final int BOARD_WIDTH = 1280;
    private final int MISSILE_SPEED =2;
	private String direction;
	private boolean visible = true;
	
	public Projectile(int x, int y, int id, String direction)
	{
		this.x = x;
		this.y = y;
		this.clientID = id;
	}

	 public void move()
	 {
      System.out.println("Missile Coordinates: x: " + x + " y: " + y);  
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
