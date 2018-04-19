package frames;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ObjectOutputStream;
import java.net.MulticastSocket;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.Timer;
import client.Client;
import sprites.Player;

public class Board extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	public Player player;
	private Timer timer;
	private final int DELAY = 15;
	private Client c;
	
	public Board(MulticastSocket s, ObjectOutputStream oos, Client c)
	{
		this.c = c;
		initBoard();
	}
	
	private void initBoard()
	{
		addKeyListener(new TAdapter());
		setFocusable(true);
		setBackground(Color.black);
		setDoubleBuffered(true);
		player = new Player(c);
		timer = new Timer(DELAY,this);
		timer.start();	
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		doDrawing(g);
		Toolkit.getDefaultToolkit().sync();
	}
	private void doDrawing(Graphics g) 
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(c.background,0, 0, this);

		//Draws players and projectile sprites
		if(c.playerMap != null)
		{
			for(Map.Entry<Integer,Player> entry : c.playerMap.entrySet())
			{
				//If statement sets radius for detection of other characters
				if(((Math.abs(player.getX() - entry.getValue().getX()) < 130) &&
					(Math.abs(player.getY() - entry.getValue().getY()) < 130)) && 
					(player.health > 0))
				{
					int spriteX = entry.getValue().playerColorX;
					int spriteY = entry.getValue().playerColorY;
					String direction = entry.getValue().direction;
					BufferedImage temp = null;
					
					//Draws character if health > 0 and draws direction of player
					if(entry.getValue().health > 0)
					{
						//Looking Up
						if(direction.equals("N") || direction.equals("NE") || direction.equals("NW"))
						{
							spriteX += 0; spriteY += 154;
							temp = c.playerSheet.getSubimage(spriteX,spriteY, 45, 35);
							entry.getValue().lastH = 35;
						}
						//Looking Right
						else if(direction.equals("E"))
						{
							spriteX += 0; spriteY += 100;
							entry.getValue().lastH = 48;
							temp = c.playerSheet.getSubimage(spriteX,spriteY, 45, 48);
						}
						//Looking Down
						else if(direction.equals("S") || direction.equals("SE") || direction.equals("SW"))
						{  
							spriteX += 0; spriteY += 0;
							temp = c.playerSheet.getSubimage(spriteX,spriteY, 45, 48);
							entry.getValue().lastH = 48;
						}
						//Looking Left
						else if(direction.equals("W"))
						{	
							spriteX += 0; spriteY += 50;
							temp = c.playerSheet.getSubimage(spriteX,spriteY, 45, 48);
							entry.getValue().lastH = 48;
						}
						
						g2d.drawImage(temp,
								entry.getValue().getX(), 
								entry.getValue().getY(), 34,34,
								this);
						
					}
					//Draws dead characters
					else
					{
						g2d.drawImage(c.dead,
								entry.getValue().getX(), 
								entry.getValue().getY(), 34,34,
								this);
					}	
				}
				//If player is dead draw the entire map so they can see everything
				else if(player.health <= 0)
				{
					int spriteX = entry.getValue().playerColorX;
					int spriteY = entry.getValue().playerColorY;
					String direction = entry.getValue().direction;
					BufferedImage temp = null;
					
					if(entry.getValue().health > 0)
					{
						//Looking Up
						if(direction.equals("N") || direction.equals("NE") || direction.equals("NW"))
						{
							spriteX += 0; spriteY += 154;
							temp = c.playerSheet.getSubimage(spriteX,spriteY, 45, 35);
							entry.getValue().lastH = 35;
						}
						//Looking Right
						else if(direction.equals("E"))
						{
							spriteX += 0; spriteY += 100;
							entry.getValue().lastH = 48;
							temp = c.playerSheet.getSubimage(spriteX,spriteY, 45, 48);
						}
						//Looking Down
						else if(direction.equals("S") || direction.equals("SE") || direction.equals("SW"))
						{  
							spriteX += 0; spriteY += 0;
							temp = c.playerSheet.getSubimage(spriteX,spriteY, 45, 48);
							entry.getValue().lastH = 48;
						}
						//Looking Left
						else if(direction.equals("W"))
						{	
							spriteX += 0; spriteY += 50;
							temp = c.playerSheet.getSubimage(spriteX,spriteY, 45, 48);
							entry.getValue().lastH = 48;
						}
						
						g2d.drawImage(temp,
								entry.getValue().getX(), 
								entry.getValue().getY(), 34,34,
								this);
						
					}
					//Draws dead characters
					else
					{
						g2d.drawImage(c.dead,
								entry.getValue().getX(), 
								entry.getValue().getY(), 34,34,
								this);
					}
				}
				
				//Get Sprite images for projectile
				BufferedImage temp2 = c.projectileSheet.getSubimage(656,135,
						 117, 117);
				BufferedImage temp3 = c.projectileSheet.getSubimage(656,270,
						 113, 114);
				
				//Draw projectiles
				for(int j = 0; j < entry.getValue().missiles.size(); ++j)
				{
					//Draw client projectiles
					if(player.getID() == entry.getKey())
					{
						if(entry.getValue().missiles.get(j).getX()%2 == 0)
						{
							g2d.drawImage(temp2,
								 entry.getValue().missiles.get(j).getX(),
								 entry.getValue().missiles.get(j).getY(),20,20,
								 this);
						}
								else
						{
							g2d.drawImage(temp3,
								 entry.getValue().missiles.get(j).getX(),
								 entry.getValue().missiles.get(j).getY(),20,20,
								this);
						}
					}
					//Draw all other players projectiles if within a certain distance
					else if( (Math.abs(player.getX() - entry.getValue().missiles.get(j).getX()) < 130) &&
							(Math.abs(player.getY() - entry.getValue().missiles.get(j).getY()) < 130)  && 
							(player.health > 0))
					{
						//Switch between two projectile Images to get a type of flickering
						if(entry.getValue().missiles.get(j).getX()%2 == 0)
						{
							g2d.drawImage(temp2,
								 entry.getValue().missiles.get(j).getX(),
								 entry.getValue().missiles.get(j).getY(),20,20,
								 this);
						}
								else
						{
							g2d.drawImage(temp3,
								 entry.getValue().missiles.get(j).getX(),
								 entry.getValue().missiles.get(j).getY(),23,23,
								this);
						}
						
					}
					else
					{
						//Switch between two projectile Images to get a type of flickering
						if(entry.getValue().missiles.get(j).getX()%2 == 0)
						{
							g2d.drawImage(temp2,
								 entry.getValue().missiles.get(j).getX(),
								 entry.getValue().missiles.get(j).getY(),20,20,
								 this);
						}
								else
						{
							g2d.drawImage(temp3,
								 entry.getValue().missiles.get(j).getX(),
								 entry.getValue().missiles.get(j).getY(),23,23,
								this);
						}
					}
				}
			}
		}
	
	}

	//Action Performed Method that executes every 15ms
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(player.health > 0)
			move();
	}
	
	//Player Move function to adjust x and y coordinates
	//Based off of Key Input
	private void move()
	{
		player.move(c);	
	}
	
	//KeyListener Key Adapter
	public class TAdapter extends KeyAdapter 
	{
        @Override
        public void keyReleased(KeyEvent e) 
        {
        	player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) 
        {
        	player.keyPressed(e);
        }
      }
}


