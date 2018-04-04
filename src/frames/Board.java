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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.Timer;

import client.Client;
import serializedMessages.GameMessage;
import sprites.Player;

public class Board extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Player player;
	private Timer timer;
	private final int DELAY = 10;
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket s;
	private GameMessage gm;
	private Client c;
	private Image image;
	//private Map<Integer, Player> playerMap;
	
	public Board(Socket s, ObjectInputStream ois, ObjectOutputStream oos, Client c)
	{
		this.ois = ois;
		this.oos = oos;
		this.s = s;
		this.c = c;
		
		player = new Player();
		gm = new GameMessage(c.getID(), "addplayer", "");
		gm.player = player;
		
		try 
		{
			oos.writeObject(gm);
			oos.flush();
			
			gm = (GameMessage)ois.readObject();
			
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(gm.getProtocol().equals("addedplayer"))
		{			
			System.out.println("Player Added Response Received: " + gm.getID());
			initBoard();
			//playerMap = gm.playerMap;
			
			for(Map.Entry<Integer,Player> entry : gm.playerMap.entrySet())
			{
				int currentID = entry.getKey();
				player = entry.getValue();
				repaint(player.getX()-1, player.getY()-1,
					player.getWidth()+2, player.getHeight()+2);
				
			}
		}
		
		
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
		//g.fillOval(player.getX(), player.getY(), 200,200);
		doDrawing(g);
		Toolkit.getDefaultToolkit().sync();
	}
	private void doDrawing(Graphics g) 
	{
		Graphics2D g2d = (Graphics2D) g;
		
		
		g2d.setClip(player.getX()+2, player.getY(), 43,90);
		g2d.drawImage(player.getImage(),player.getX(), player.getY(), this);
		
		
		//Iterate through whole map and draw everyone from the array.
	}


	@Override
	public void actionPerformed(ActionEvent e) 
	{
		move();
		
		
	}

	private void move()
	{
		player.move();
		
		//repaint(player.getX()-1, player.getY()-1,
				//player.getWidth()+2, player.getHeight()+2);
		gm = new GameMessage(c.getID(), "movement",player.getX()-1, player.getY()-1);
		
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


