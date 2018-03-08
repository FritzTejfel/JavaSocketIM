package com.InstantMessaging.Server;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {

	private JTextField userText;
	private JTextArea chatWindow;
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	private ServerSocket server;
	private Socket connection;
	
	public Server() {
		
		super("Istant Messager");
		
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {

						sendMessage(e.getActionCommand());
						userText.setText("");
						
					}

				}
				
				);
		
		add(userText, BorderLayout.NORTH);
		
		chatWindow = new JTextArea();	
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
				
	}
	
	public void startRun() {
		
		try {
			
			server = new ServerSocket(6789, 100);
			
			while(true) {
				
				try {
					
					waitForConnection();
					setupStream();
					whileChatting();
					
				} catch(EOFException e) {
					
					showMessage("\n Server ended the Connection!");
					
				} finally {
					
					closeCrap();
					
				}
				
			}
			
		} catch(IOException e) {
			
			e.printStackTrace();
		}
	}

	private void closeCrap() {

		showMessage("\n Closing connections... \n");
		ableToType(false);
		
		try {
			
			output.close();
			input.close();
			connection.close();			
			
		} catch(IOException e) {
			
			e.printStackTrace();
			
		}
		
	}

	private void setupStream() throws IOException {
		
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup!\n");
		
	}

	private void waitForConnection() throws IOException {

		showMessage("Waiting for someone to connect...\n");
		
		connection = server.accept();
		
		showMessage("Now connect to " + connection.getInetAddress().getHostName());
		
	}
	
	private void whileChatting() throws IOException {
		
		String msg = "Your are now connected!";
		sendMessage(msg);
		ableToType(true);
		
		do {
			
			try {
				
				msg = (String) input.readObject();
				sendMessage("\n" + msg);
				
			} catch(ClassNotFoundException e) {
				
				showMessage("\n idk wtf that user send!");
				
			}
			
		} while(!msg.equals(("CLIENT - END")));
		
	}
	
	private void ableToType(final boolean tof) {
		
		SwingUtilities.invokeLater(
				
				new Runnable() {
					
					public void run() {
						
						userText.setEditable(tof);
						
					}
					
				} 
					
			);
		
	}

	private void showMessage(final String msg) {

		SwingUtilities.invokeLater(
					
			new Runnable() {
				
				public void run() {
					
					chatWindow.append(msg);
					
				}
				
			} 
				
		);
		
	}

	private void sendMessage(String msg) {
		
		try {
			
			output.writeObject("SERVER - " + msg);
			output.flush();
			
			showMessage("\nSERVER - " + msg);
			
		} catch(IOException e) {
			
			chatWindow.append("\n Error: I cant send that message");
			
		}
		
	}
}
