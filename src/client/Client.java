package client;

import java.awt.EventQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

import frames.MainFrame;
import serializedMessages.MovementMessage;

public class Client extends Thread
{
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private MainFrame ex;
	private Socket s;
	
	public Client() throws InvocationTargetException, InterruptedException
	{
		
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		boolean connect = false;
		EventQueue.invokeAndWait(() -> {
			ex = new MainFrame();
	        ex.setVisible(true);     
	        });
		while(true)
		{
			
				
				
				while(!connect) 
				{
					//When commented out this line 46 it does not work...
					System.out.println("Hello");
					
					if(!ex.getWait())
					{
						try
						{
							System.out.println("Trying to connect to " + ex.getTxtIpAddress() + ":" + ex.getTxtPort());
							s = new Socket(ex.getTxtIpAddress(), Integer.parseInt(ex.getTxtPort()) );
							connect = true;
							ex.setWait(true);
							System.out.println("Connected to " + ex.getTxtIpAddress() + ":" + ex.getTxtPort());
							ois = new ObjectInputStream(s.getInputStream());
							oos = new ObjectOutputStream(s.getOutputStream());
							this.start();
							}
						catch(SocketException se)
						{
							System.out.println("From Client: " + se );
							ex.setWait(true);
						}
						catch(IOException ioe)
						{
							System.out.println("Reading Input Error from Client Main: " + ioe);
						}	
					}
				}
		}	
	}
	
	public void run()
	{
		try 
		{
			while(true) 
			{
				MovementMessage gm = (MovementMessage)ois.readObject();
			}
		}
		catch (IOException ioe) 
		{
			System.out.println("ioe in ChatClient.run(): " + ioe.getMessage());
		} 
		catch (ClassNotFoundException cnfe) 
		{
			System.out.println("cnfe: " + cnfe.getMessage());
		}
	}
	
	public static void main(String [] args) throws InvocationTargetException, InterruptedException
	{
		Client gc = new Client();
	}
}

