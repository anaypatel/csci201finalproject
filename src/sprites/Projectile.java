package sprites;

import java.io.Serializable;

public class Projectile implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int x, y;
	public int clientID;
	
	private final int BOARD_WIDTH = 1280;
	private final int BOARD_HEIGHT = 720;
    private final int MISSILE_SPEED =5;
	private String direction;
	private boolean visible = true;
	
	public Projectile( int x, int y, int id, String direction)
	{
		this.x = x;
		this.y = y;
		this.clientID = id;
		this.direction = direction;
	}

	 public void move()
	 {
		 if(direction.equals("E"))
		 x += MISSILE_SPEED;
		 else if(direction.equals("S"))
		 y += MISSILE_SPEED;
		 else if(direction.equals("W"))
		 x -= MISSILE_SPEED;
		 else if(direction.equals("N"))
		 y -= MISSILE_SPEED;
		 else if(direction.equals("NW"))
		 {
			 y -= MISSILE_SPEED;
			 x -= MISSILE_SPEED;
		 }
		else if(direction.equals("SE"))
		{
			 y += MISSILE_SPEED;
			 x += MISSILE_SPEED;
		}
		else if(direction.equals("NE"))
		{
			 y -= MISSILE_SPEED;
			 x += MISSILE_SPEED;
		}			 
		else if(direction.equals("SW"))
		{
			 y += MISSILE_SPEED;
			 x -= MISSILE_SPEED;
		}
			 
        if (x > BOARD_WIDTH + 3 || x < -3 || y < -3 || y > BOARD_HEIGHT + 3) 
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
		public void setVisible(boolean state)
		{
			this.visible = false;
		}

}
