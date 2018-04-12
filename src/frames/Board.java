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
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.Timer;

import client.Client;
import serializedMessages.GameMessage;
import sprites.Player;
import sprites.Projectile;

public class Board extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	public Player player;
	private Timer timer;
	private final int DELAY = 15;
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
			for(Map.Entry<Integer,Player> entry : c.playerMap.entrySet())
			{
				//g2d.setClip(entry.getValue().getX()+2, entry.getValue().getY(), 43,90);
				
				//5,5 start on sprite sheet /8w / 8h
				//6x6
				/*
				g2d.drawImage(c.playerSprite,
						entry.getValue().getX() + 2, entry.getValue().getY() + 2,
						entry.getValue().getX() + 8, entry.getValue().getY() +8,
						5*8, 5*8,
						(5+1)*8,  (5+1)*8,
						this);
				
				*/
				g2d.drawImage(c.playerSprite,
						entry.getValue().getX(), entry.getValue().getY(),
						entry.getValue().getX() + 50, entry.getValue().getY() + 50,
						0, 0, 50, 50,
						this);
				
				for(int j = 0; j < entry.getValue().missiles.size(); ++j)
				{
					System.out.println("missile drawn?");
					
					
					/*g2d.drawImage(c.playerSprite,
							 entry.getValue().missiles.get(j).getX(),entry.getValue().missiles.get(j).getY(),
							 entry.getValue().missiles.get(j).getX() + 50,entry.getValue().missiles.get(j).getY() + 50,
							0, 0, 50, 50,
							this);*/
					
					g2d.drawImage(c.projectileSprite,
							 entry.getValue().missiles.get(j).getX(),
							 entry.getValue().missiles.get(j).getY(),
							 entry.getValue().missiles.get(j).getX() + 50,
							 entry.getValue().missiles.get(j).getY() + 50,
							 
							 
							this);
					
					 entry.getValue().missiles.get(j).move();
				}
				
			}
		}
	
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		move();	
		updateMissiles();
	}
	
	 private void updateMissiles() {

	        ArrayList<Projectile> missiles = player.missiles;

	        for (int i = 0; i < missiles.size(); i++) {

	            Projectile missile = missiles.get(i);

	            if (missile.isVisible()) {

	                missile.move();
	            } else {

	            	System.out.println("Removed missile");
	                missiles.remove(i);
	            }
	        }
	    }
	private void move()
	{
		if(player != null)
		{
			player.move(c);	
			repaint();
		}
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


