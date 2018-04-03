package frames;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
	private Player player;
	private Timer timer;
	private final int DELAY = 10;
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket s;
	private GameMessage gm;
	private Client c;
	
	public Board(Socket s, ObjectInputStream ois, ObjectOutputStream oos, Client c)
	{
		this.ois = ois;
		this.oos = oos;
		this.s = s;
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
		doDrawing(g);
		Toolkit.getDefaultToolkit().sync();
	}
	private void doDrawing(Graphics g) 
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(player.getImage(),player.getX(), player.getY(), this);
		
	}


	@Override
	public void actionPerformed(ActionEvent e) 
	{
		move();
	}

	private void move()
	{
		player.move();
		repaint(player.getX()-1, player.getY()-1,
				player.getWidth()+2, player.getHeight()+2);
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


