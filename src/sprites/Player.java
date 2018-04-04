package sprites;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.Serializable;

import javax.swing.ImageIcon;

public class Player implements Serializable
{
	public static final long serialVersionUID = 1;
	private int mx;
	private int my;
	private int x = 300;
	private int y = 300;
	
	//private Image image;
	
	public Player()
	{
		
	}
	
	
	
	public void move()
	{
		//Need to set boundaries on border later!
		this.x = this.x + mx;
		this.y = this.y + my;
	}
	
	public int getX()
	{
		return this.x;
	}
	public int getY()
	{
		return this.y;
	}
		
//	public Image getImage()
//	{
//		return this.image;
//	}
	
	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_A)
		{
			this.mx = -2;
		}
		if(key == KeyEvent.VK_D)
		{
			this.mx = 2;
		}
		
		if(key == KeyEvent.VK_W)
		{
			this.my = -2;
		}
		
		if(key == KeyEvent.VK_S)
		{
			this.my = 2;
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
