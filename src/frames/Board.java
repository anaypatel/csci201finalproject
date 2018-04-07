package frames;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
	private ObjectOutputStream oos;
	private GameMessage gm;
	private Client c;
	public Board(Socket s, ObjectOutputStream oos, Client c)
	{
		this.oos = oos;
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
		//System.out.println("Paint Component Call");
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		doDrawing(g);
		Toolkit.getDefaultToolkit().sync();
	}
	private void doDrawing(Graphics g) 
	{
		Graphics2D g2d = (Graphics2D) g;

		for(Map.Entry<Integer,Movement> entry : c.playerMap.entrySet())
		{
			//System.out.println("Drawing " + entry.getValue().clientID + "  " + entry.getValue().x + "  " + entry.getValue().y);
			//int currentID = entry.getKey();
			//System.out.println("ID: " + currentID + "  drawn");
			g2d.setClip(entry.getValue().x+2, entry.getValue().y, 43,90);
			g2d.drawImage(player.getImage(),entry.getValue().x, entry.getValue().y, this);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		move();	
	}
	private void move()
	{
		player.move();	
	}
	public class TAdapter extends KeyAdapter 
	{
        @Override
        public void keyReleased(KeyEvent e) 
        {
        	//System.out.println("KeyReleased: " + player.getX() + "  "  + player.getY());
            player.keyReleased(e);
            gm = new GameMessage(c.getID(), "movement", player.getX(), player.getY());     	
            try 
            {
				oos.flush();
			 	oos.writeObject(gm);
			} catch (IOException e1) 
            {
				e1.printStackTrace();
			}      	
        }

        @Override
        public void keyPressed(KeyEvent e) 
        {
        	//System.out.println("KeyPressed: " + player.getX() + "  "  + player.getY());
        	player.keyPressed(e);
        	gm = new GameMessage(c.getID(), "movement",player.getX(), player.getY());
        	try 
        	{
				oos.flush();
			 	oos.writeObject(gm);
			} catch (IOException e1) 
        	{
				e1.printStackTrace();
			}
        }
      }
}


