package sprites;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import client.Client;
import serializedMessages.GameMessage;

public class Player implements Serializable
{
	private String playerSprite = "player";
	private int mx;
	private int my;
	private int x = 300;
	private int y = 300;
	private int clientID;
	
	public ArrayList<Projectile> missiles = new ArrayList<Projectile>();
	
	public Player(int clientID, int x, int y, String spriteName)
	{
		//missiles = new ArrayList<Projectile>();
		this.clientID = clientID;
		this.x = x;
		this.y = y;
	}
	
	public Player()
	{
		
	}
	
	 public void fire() 
	 {
		 System.out.println("Missile added");
	       missiles.add(new Projectile(x, y,clientID, ""));
		 
		 /*	
		 	byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			System.out.println(c.getID() + "projectile  " + this.x + this.y);
			GameMessage gm = new GameMessage(c.getID(), "projectile",this.x, this.y);
			data = c.serializeGM(c.baos, gm, c.oos);
			c.sendData(data);
		*/
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
		if(x < 1220 && x > 0)
		{
			this.x = this.x + mx;
		}		
		else
		{
			if(x >= 1220)
				x -= 1;
			if(x <= 0 )
				x += 1;
		}
		if(y > 0 && y < 589 )
		{
			this.y = this.y + my;
		}
		else
		{
			if(y >= 589)
				y -= 1;
			if(y <= 0 )
				y += 1;
		}
		//System.out.println("x: " + this.x + " y: " + this.y);
		if(mx != 0 || my != 0)
		{
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			GameMessage gm = new GameMessage(c.getID(), "movement");
			gm.player = this;
			data = c.serializeGM(c.baos, gm, c.oos);
        	c.sendData(data);
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
	            fire();
	            System.out.println("Projectile fired");
	    }
		if(key == KeyEvent.VK_A)
		{
			this.mx = -3;
		}
		if(key == KeyEvent.VK_D)
		{
			this.mx = 3;
		}
		if(key == KeyEvent.VK_W)
		{
			this.my = -3;
		}
		if(key == KeyEvent.VK_S)
		{
			this.my = 3;
		}
		
		
	}
	
	public void keyReleased(KeyEvent e)
	{
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_A)
		{
			this.mx =0;
		}
		if(key == KeyEvent.VK_D)
		{
			this.mx = 0;
		}
		if(key == KeyEvent.VK_W)
		{
			this.my = 0;
		}
		if(key == KeyEvent.VK_S)
		{
			this.my = 0;
		}
	}
}
