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
import serializedMessages.GameMessage;
import server.Movement;
import sprites.Player;

public class Board extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	public Player player;
	private Timer timer;
	private final int DELAY = 10;
	private GameMessage gm;
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
		player = new Player();
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

		if(c.playerMap != null)
		{
			for(Map.Entry<Integer,Movement> entry : c.playerMap.entrySet())
			{
				g2d.setClip(entry.getValue().x+2, entry.getValue().y, 43,90);
				
				
				g2d.drawImage(c.loadImage(player.getSprite())
						,entry.getValue().x + 2, entry.getValue().y + 2, this);
				
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
			repaint();
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


