package sprites;

import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player 
{
	private int mx;
	private int my;
	private int x = 20;
	private int y = 30;
	private int w;
	private int h;
	private Image image;
	
	public Player()
	{
		loadImage();
	}
	
	private void loadImage()
	{
		ImageIcon ic = new ImageIcon("src/resources/player.png");
		this.image = ic.getImage();
		this.w = image.getWidth(null);
		this.h= image.getHeight(null);
	}
	
	public void move()
	{
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
	public int getWidth()
	{
		return this.w;
	}
	public int getHeight()
	{
		return this.y;
	}
	public Image getImage()
	{
		return this.image;
	}
	
	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_LEFT)
		{
			this.mx = -2;
		}
		if(key == KeyEvent.VK_RIGHT)
		{
			this.mx = 2;
		}
		
		if(key == KeyEvent.VK_UP)
		{
			this.my = -2;
		}
		
		if(key == KeyEvent.VK_DOWN)
		{
			this.my = 2;
		}
		
		
	}
	
	public void keyReleased(KeyEvent e)
	{
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_LEFT)
		{
			this.mx =0;
		}
		if(key == KeyEvent.VK_RIGHT)
		{
			this.mx = 0;
		}
		
		if(key == KeyEvent.VK_UP)
		{
			this.my = 0;
		}
		
		if(key == KeyEvent.VK_DOWN)
		{
			this.my = 0;
		}
	}
}
