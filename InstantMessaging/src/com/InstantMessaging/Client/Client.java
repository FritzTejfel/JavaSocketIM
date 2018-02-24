package com.InstantMessaging.Client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow;
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	private String message = "";
	private String ServerIP;
	private Socket connection;
	
	public Client(String host) {
		
		super("IM");
		
		ServerIP = host;
		
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
						sendMessage(e.getActionCommand());
						userText.setText(" ");
						
					}
				}
				
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
		
	}
	
	public void startRunning() {
		
		try {
			
			connectToServer();
			setupStream();
			whileChatting();
			
		} catch(EOFException e) {
			
			showMessage("\n Client terminated connection");
			
		} catch(IOException e) {
			
			e.printStackTrace();
			
		} finally {
			
			closeCrap();
			
		}
		
		
	}

	private void closeCrap() {
		
		sendMessage("\n Closing crap down...");		
		ableToType(false);
		
		try {
			
			output.close();
			input.close();
			connection.close();			
			
		} catch(IOException e) {
			
			e.printStackTrace();
			
		}
		
	}

	private void whileChatting() throws IOException {
		
		ableToType(true);
		
		do {
			
			try {
				
				message = (String) input.readObject();
				
				showMessage("\n" + message);
				
			} catch(ClassNotFoundException e) {
				
				showMessage("\n I dont know that object type");
			}			
			
		} while(!message.equals("CLIENT - END"));
		
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
		

	private void setupStream() throws IOException {
		
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup!\n");
		
	}

	private void connectToServer() throws IOException {
		
		showMessage("Attempting connection...\n");
		
		connection =new Socket(InetAddress.getByName(ServerIP), 6789);
		
		showMessage("Connection to: " + connection.getInetAddress().getHostName());
		
	}
	
	private void sendMessage(String msg) {
		
		try {
			
			output.writeObject("CLIENT - " + msg);
			output.flush();
			
			showMessage("\nCLIENT - " + msg);
			
		} catch(IOException e) {
			
			chatWindow.append("\n Error: I cant send that message");
			
		}
		
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


}
