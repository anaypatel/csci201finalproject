package frames;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
		g2d.drawImage(c.background,0, 0, null);

		if(c.playerMap != null)
		{
			for(Map.Entry<Integer,Player> entry : c.playerMap.entrySet())
			{
				if( (Math.abs(player.getX() - entry.getValue().getX()) <1280) &&
						(Math.abs(player.getY() - entry.getValue().getY()) < 720))
				{
					int spriteX = entry.getValue().playerColorX;
					int spriteY = entry.getValue().playerColorY;
					
					String direction = entry.getValue().direction;
					System.out.println("Direction: " + direction);
					
					if(direction.equals("N") || direction.equals("NE") || direction.equals("NW"))
					{
						spriteX += 0; spriteY += 145;
					}
					else if(direction.equals("E"))
					{
						spriteX += 0; spriteY += 100;
					}
					else if(direction.equals("S") || direction.equals("SE") || direction.equals("SW"))
					{
						spriteX += 0; spriteY += 0;
					}
					else if(direction.equals("W"))
					{	
						spriteX += 0; spriteY += 50;
					}
						
					BufferedImage temp = c.playerSheet.getSubimage(spriteX, 
										 spriteY, 48, 51);
					g2d.drawImage(temp,
							entry.getValue().getX(), 
							entry.getValue().getY(), 70,70,
							this);
				}
				
				BufferedImage temp2 = c.projectileSheet.getSubimage(656,135,
						 117, 117);
				BufferedImage temp3 = c.projectileSheet.getSubimage(656,270,
						 113, 114);
				for(int j = 0; j < entry.getValue().missiles.size(); ++j)
				{
					
					if( (Math.abs(player.getX() - entry.getValue().missiles.get(j).getX()) < 1280) &&
							(Math.abs(player.getY() - entry.getValue().missiles.get(j).getY()) < 720))
					{
						if(entry.getValue().missiles.get(j).getX()%2 == 0)
						{
							g2d.drawImage(temp2,
								 entry.getValue().missiles.get(j).getX(),
								 entry.getValue().missiles.get(j).getY(),40,40,
								 this);
						}
								else
						{
							g2d.drawImage(temp3,
								 entry.getValue().missiles.get(j).getX(),
								 entry.getValue().missiles.get(j).getY(),40,40,
								this);
						}
						
					}
				}
			}
		}
	
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		move();
	}

	private void move()
	{
		player.move(c);	
	}
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


