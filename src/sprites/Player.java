package sprites;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.net.DatagramPacket;
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
	//private int w;
	//private int h;
	//public static Image image;
	
	public Player(int clientID, int x, int y, String spriteName)
	{
		this.clientID = clientID;
		this.x = x;
		this.y = y;
	}
	
	public Player()
	{
		
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
		//Need to set boundaries on border later!
	
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
		System.out.println("x: " + this.x + " y: " + this.y);
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
	/*
	public int getWidth()
	{
		return this.w;
	}
	public int getHeight()
	{
		return this.h;
	}
	
	  public Image getImage()
	 
	{
		return this.image;
	}
	*/
	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		
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
