package client;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import frames.Board;
import frames.MainFrame;
import serializedMessages.GameMessage;
import server.Movement;

public class Client extends Thread
{
	public int clientID = -1;
	private MainFrame ex;
	public MulticastSocket socket;
	private Board board;
	public GameMessage gm = null;
	public boolean running = true;
	private MulticastSocket ms = null;
	public ByteArrayOutputStream baos = null;
	private ByteArrayInputStream bais = null;
	public ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	public Map<Integer, Movement> playerMap;
	
	public Client()
	{
		playerMap = new HashMap<Integer, Movement>();
		boolean connected = false;
	
		try 
		{
			socket = new MulticastSocket();
		} 
		catch (IOException e2) 
		{
			e2.printStackTrace();
		}
		
		EventQueue.invokeLater(() -> 
		{
			ex = new MainFrame(this.socket);
	        ex.setVisible(true);    
	    });
		
		while(!connected)
		{
			try 
			{
				if(socket.getInetAddress().isReachable(10000))
				{
					System.out.println("Connected! Socket Address: " + socket.getInetAddress());
					connected = true;
			        ex.getContentPane().removeAll();
			        board = new Board(this.socket, oos, this);
			        ex.setContentPane(board);
			        ex.setLocationRelativeTo(null);
			        ex.setResizable(false);
			        ex.getContentPane().revalidate();
			        ex.repaint();
			        ex.requestFocusInWindow();
			        ex.addKeyListener(board.getKeyListeners()[0]);
			        KeyListener[] e = ex.getKeyListeners();
			        ex.setName("Game Board");
			     
				}
			} 
			catch(SocketException se)
			{
				System.out.println("From Client: " + se );
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			catch(NullPointerException npe)
			{
				//Do nothing
			}
		}
		
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		
		while(this.clientID == -1) 
		{
			System.out.println("here");
			GameMessage assignID = new GameMessage(-1, "assignid", "" + this.getId());	        
	        data = serializeGM(baos, assignID, oos);
			try 
			{
				sendData(data);
				data = new byte[1024];
				packet = new DatagramPacket(data, data.length);
		        socket.receive(packet);		        
		        gm = deSearializeGM(data, bais, ois);	    
		       // System.out.println(gm.getID() + " : " + gm.getProtocol() + "  " + gm.getMessage()
		       // + "\nThis local Port: " + socket.getLocalPort() );
		        if(gm.getProtocol().equalsIgnoreCase("assignedID") 
		        	&& gm.getMessage().trim().equalsIgnoreCase( String.valueOf(socket.getLocalPort())))
		        {
		        	this.clientID = gm.getID();
		        	playerMap = gm.playerMap;	
		        	
		        }
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
		}
		   this.start();
		   board.repaint();
	}
	
	
	public Image loadImage(String name)
	{
		System.out.println("Image loaded");
		ImageIcon ic = new ImageIcon("src/resources/" + name + ".png");
		Image image = ic.getImage();
		
		//this.w = image.getWidth(null);
		//this.h= image.getHeight(null);
		
		return image;
	}
	
	
	@SuppressWarnings("resource")
	public synchronized  void run()
	{
		try 
		{
			ms = new MulticastSocket(4000);
			InetAddress group = InetAddress.getByName("224.0.0.1");
			ms.joinGroup(group);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		byte[] data;
		DatagramPacket packet;

		while(true)
		{
			 data = new byte[1024];
			 packet = new DatagramPacket(data, data.length);			
			try 
			{
				ms.receive(packet);
			} catch (IOException e) 
			{
				e.printStackTrace();
			} 
			gm = deSearializeGM(data, bais, ois);	
			if(gm.getProtocol().equalsIgnoreCase("movementupdate"))
			{
				playerMap = gm.playerMap;
			}  
		}
	}
	
	
	
	public GameMessage deSearializeGM(byte[] data, ByteArrayInputStream bais, 
				  ObjectInputStream ois )
	{
		GameMessage gm1 = null;
		try 
		{
			bais = new ByteArrayInputStream(data);
			ois = new ObjectInputStream(bais);
			gm1 = (GameMessage)ois.readObject();
		} 
		catch (ClassNotFoundException | IOException e) 
		{
			e.printStackTrace();
		}	
		return gm1;
	}

	
	
	public byte[] serializeGM(ByteArrayOutputStream baos, GameMessage gm2, 
				 ObjectOutputStream oos)
	{
		byte [] data = null;
		baos = new ByteArrayOutputStream();
		try 
		{
			oos = new ObjectOutputStream(baos);
			oos.writeObject(gm2);
			oos.flush();
			data = baos.toByteArray();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		return data;
	}
	
	
	
	
	
	public void sendData(byte[] data)
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, socket.getInetAddress(), socket.getPort());
		try 
		{
			socket.send(packet);
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public int getID()
	{
		return this.clientID;
	}
	
	
	
	
	public static void main(String [] args) throws InvocationTargetException, InterruptedException
	{
		Client gc = new Client();
	}
}

