package frames;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
					g2d.drawImage(c.playerSprite,
							entry.getValue().getX(), 
							entry.getValue().getY(),
							entry.getValue().getX() + 25, 
							entry.getValue().getY() + 25,
							0, 0, 50, 50,
							this);
				}
				for(int j = 0; j < entry.getValue().missiles.size(); ++j)
				{
					
					if( (Math.abs(player.getX() - entry.getValue().missiles.get(j).getX()) < 1280) &&
							(Math.abs(player.getY() - entry.getValue().missiles.get(j).getY()) < 720))
					{
						if(entry.getValue().missiles.get(j).getX()%2 == 0)
						{
							g2d.drawImage(c.proj1,
								 entry.getValue().missiles.get(j).getX(),
								 entry.getValue().missiles.get(j).getY(),
								 this);
						}
						else
						{
							g2d.drawImage(c.proj2,
								 entry.getValue().missiles.get(j).getX(),
								 entry.getValue().missiles.get(j).getY(),
								 entry.getValue().missiles.get(j).getX() + 15,
								 entry.getValue().missiles.get(j).getY() + 15,
								 0,0,20,20,
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


