package sprites;

import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;

import client.Client;

public class Player implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String playerSprite = "player";
	private int mx;
	private int my;
	private int x;
	private int y;
	private int clientID;
	public int port;
	private static Client c;
	public String direction = "E";
	public ArrayList<Projectile> missiles = new ArrayList<Projectile>();
	
	public Player(Client c)
	{
		Player.c = c;
		this.port = c.socket.getPort();
	}
	
	 public Player() {
		// TODO Auto-generated constructor stub
	}

	public void fire() 
	 {
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

	      // missiles.add(new Projectile(x, y,clientID, direction));
		// System.out.println("Sent Projectile");
	       c.sendPlayerUpdate("projectile");

	  }
	 
	public ArrayList<Projectile> getMissiles() 
	{
		return missiles;
	}
	
	public int getID()
	{
		return this.clientID;
	}
	public void setClientID(int ID)
	{
		this.clientID = ID;
	}
	
	public String getSprite()
	{
		return playerSprite;
	}
	
	public void move(Client c)
	{
		if(x < 1280 && x > 0)
		{
			this.x = this.x + mx;
		}		
		else
		{
			if(x >= 1280)
				x -= 1;
			if(x <= 0 )
				x += 1;
		}
		if(y > 0 && y < 720 )
		{
			this.y = this.y + my;
		}
		else
		{
			if(y >= 720)
				y -= 1;
			if(y <= 0 )
				y += 1;
		}

		if(mx != 0 || my != 0)
		{
		//	System.out.println("Sent movement");
			c.sendPlayerUpdate("movement");
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
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}

	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_SPACE) 
		{
			if(missiles.size() < 5)
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
