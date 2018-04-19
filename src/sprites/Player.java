package sprites;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import client.Client;

public class Player implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int mx;
	private int my;
	private int x;
	private int y;
	private int clientID;
	public int health = 5;
	public int kills = 0;
	public int shots = 0;
	public int hits = 0;
	public int deaths = 0;
	public int wins = 0;
	private static Client c;
	public String direction = "E";
	public int lastW = 45, lastH = 48;
	public ArrayList<Projectile> missiles = new ArrayList<Projectile>();
	public int playerColorY = 0, playerColorX = 0;
	public String username;

	public Player() {}
	
	public Player(Client _c)
	{
		c = _c;
	}
	
	//Need constructor later for adjusting kills/shots/hits/deaths
	
	//Signal Server that a projectile was fired from client
	public void fire() 
	{
		if(health > 0)
		{
			this.shots += 1;
			c.sendPlayerUpdate("projectile");
		}
	}
	 
	//Return player projectiles
	public ArrayList<Projectile> getMissiles() 
	{
		return missiles;
	}
	
	//return player ID associated with Client ID
	public int getID()
	{
		return this.clientID;
	}
	//Set player ID as client ID
	public void setClientID(int ID)
	{
		this.clientID = ID;
	}

	//Adjusts players coordinates based on key input
	public void move(Client c)
	{
		System.out.println("x:" + x + "  y:" + y);
		//Bounds for border of screen
		if(x < 1240 && x > 0)
		{
			this.x = this.x + mx;
		}		
		else
		{
			if(x >= 1240)
				x -= 5;
			if(x <= 0 )
				x += Math.abs(x) + 1 ;
		}
		if(y > 0 && y < 656)
		{
			this.y = this.y + my;
		}
		else
		{
			if(y >= 656)
				y -= 5;
			if(y <= 0)
				y += 5;
		}

		//Determines direction of sprite
		//X:LEFT, Y:UP = Diagonal top left
		 if(mx < 0 && my < 0)
		 {
			 direction = "NW"; 		 
		 }
		 //X:RIGHT, Y:DOWN = Diagonal bottom right
		 else if (mx > 0 && my > 0)
		 {
			 direction = "SE"; 		 
		 }
		 //X:RIGHT, Y:UP = Diagonal Top Right
		 else if(mx > 0 && my < 0)
		 {
			 direction = "NE";
		 }
		 //X:LEFT, Y:DOWN = Diagonal bottom left
		 else if(mx < 0 && my > 0)
		 {
			 direction = "SW";
		 }
		 else if(mx > 0)
			 direction = "E";
		 else if(mx < 0)
			 direction  = "W";
		 else if(my > 0)
			 direction ="S";
		 else if(my < 0)
			 direction = "N";
		
		
		 //Checks for collisions between player sprites
		 for(Map.Entry<Integer,Player> entry : c.playerMap.entrySet())
			{
				if(this.clientID != entry.getKey() && (entry.getValue().health > 0) &&this.getBounds().intersects(entry.getValue().getBounds()))
				{
					if(direction.equals("N"))
					{
						this.y += 5;
					}
					if(direction.equals("NE"))
					{
						this.x += -5;
						this.y += 5;
					}
					if(direction.equals("NW"))
					{
						this.x += 5;
						this.y += 5;	
					}
					if(direction.equals("E"))
					{
						this.x += -5;	
					}
					if(direction.equals("SE"))
					{
						this.x += -5;
						this.y += -5;
					}
					if(direction.equals("SW"))
					{
						this.x += 5;
						this.y += -5;	
					}
					if(direction.equals("S"))
					{
						this.y += -5;
					}
					if(direction.equals("W"))
					{
						this.x += 5;	
					}
				}
			}
		//If movement then notify server of new player coordinates
		if(mx != 0 || my != 0)
		{
			c.sendPlayerUpdate("movement");
		}
	}
	
	//Getters and setters for X/Y coordinates
	public int getX()
	{
		return this.x;
	}
	public int getY()
	{
		return this.y;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	//returns boundaries of objects moving
	public Rectangle getBounds()
	{
		return new Rectangle(x, y, lastW - 13, lastH - 13);
	}
	
	//For capturing key input and adjusting direction progression
	//of sprite
	public void keyPressed(KeyEvent e)
	{	
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_SPACE) 
		{
			
			if(c.playerMap.get(clientID).missiles.size() < 5)
			{
	            fire();
			}   
	    }
		if(key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT)
		{
			this.mx = -3;
		}
		if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT)
		{
			this.mx = 3;
		}
		if(key == KeyEvent.VK_W || key == KeyEvent.VK_UP)
		{
			this.my = -3;
		}
		if(key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)
		{
			this.my = 3;
		}
		
		if(key == KeyEvent.VK_ESCAPE) {
			c.leaderboard = true;
		}
		
		c.sendPlayerUpdate("movement");
	}
	
	public void keyReleased(KeyEvent e)
	{
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT)
		{
			this.mx =0;
		}
		if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT)
		{
			this.mx = 0;
		}
		if(key == KeyEvent.VK_W || key == KeyEvent.VK_UP)
		{
			this.my = 0;	
		}
		if(key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)
		{
			this.my = 0;	
		}
	}
}
