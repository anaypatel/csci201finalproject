package client;

import java.awt.EventQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import frames.MainFrame;
import serializedMessages.MovementMessage;

public class Client extends Thread
{
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	
	public Client()
	{
		
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		
		while(true)
		{
			System.out.print("Please enter an IP address: ");
			try
			{
				String hostname = br.readLine();
				System.out.print("Please enter a port number: ");
				int port = Integer.parseInt(br.readLine());
				
				System.out.println("Trying to connect to " + hostname + ":" + port);
				Socket s = new Socket(hostname, port);
				System.out.println("Connected to " + hostname + ":" + port);
				ois = new ObjectInputStream(s.getInputStream());
				oos = new ObjectOutputStream(s.getOutputStream());
				this.start();
				Scanner scan = new Scanner(System.in);
				
				EventQueue.invokeLater(() -> {
					MainFrame ex = new MainFrame();
		            ex.setVisible(true);
		        });
				
				//This is where we keep writing messages. 
				while(true) 
				{
					
					
					
					
					
					
					
					
					
					
					boolean validCommand = false;
					String line = scan.nextLine();
					System.out.println("");
					
					
					
					
					
					
					
					
					
				}
			}
			catch(IOException ioe)
			{
				System.out.println("Reading Input Error from Client Main: " + ioe);
			}
			//Need to add catch for ServerSocket Timeout
			
			
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
	
	public static void main(String [] args)
	{
		Client gc = new Client();
	}
}

