package com.maspain.chitchatserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

public class Server {
	
	private ServerSocket serverSocket;
	
	public Hashtable<Socket, DataOutputStream> outputStreams = new Hashtable<Socket, DataOutputStream>();
	public HashSet<User> users = new HashSet<User>();
	
	private LinkedList<String> serverLog = new LinkedList<String>();
	private int maxLog = 2000;
	
	public Server(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		String s = "Listening on " + serverSocket;
		System.out.println(s);
		log(s);
		new Listener(serverSocket, this);
	}
	
	Enumeration<DataOutputStream> getOutputStreams() {
		return outputStreams.elements();
	}
	
	public User[] getUsers() {
		User[] theUsers = new User[users.size()];
		Iterator<User> i = users.iterator();
		int count = 0;
		while (i.hasNext()) {
			theUsers[count++] = i.next();
		}
		return theUsers;
	}
	
	public String getUsersAsString() {
		String usersString = "";
		for (User user : getUsers()) {
			if (user.istyping()) usersString += "* ";
			else usersString += "  ";
			usersString += user.getName() + "\n";
		}
		return usersString;
	}
	
	public void sendToAll(String message) {
		// Synchronize this on outputStreams to avoid problems when another thread calls removeConnection()
		synchronized (outputStreams) {
			for (Enumeration<DataOutputStream> e = getOutputStreams(); e.hasMoreElements();) {
				DataOutputStream dout = e.nextElement();
				try {
					dout.writeUTF(message);
				} catch (IOException ie) {
					String s = ie.toString();
					System.out.println(s);
					log(s);
					ie.printStackTrace();
				}
			}
		}
	}
	
	// Removes a socket and its corresponding output stream from the two hash tables
	public void removeConnection(User user) {
		// Synchronize on outputStreams to avoid problems when another thread calls sendToAll()
		synchronized (outputStreams) {
			String s = "Removing connection to " + user.getSocket();
			System.out.println(s);
			log(s);
			
			// Remove the client from the user list and send a DISCONNECT command to all users in the event of an crashed client
			if (users.contains(user)) {
				users.remove(user);
				sendToAll((new Packet(Packet.DISCONNECT, user.getName(), getUsersAsString())).getData());
			}
			
			// Remove it from our hash table
			outputStreams.remove(user.getSocket());
			
			// Make sure it's closed
			try {
				user.getSocket().close();
			} catch (IOException ie) {
				s = "Error closing " + user.getSocket();
				System.out.println(s);
				log(s);
				ie.printStackTrace();
			}
		}
	}
	
	public void log(String message) {
		if (serverLog.size() > maxLog) serverLog.removeFirst();
		serverLog.add(getTimeStamp() + " " + message);
	}
	
	public String getLog() {
		String log = "";
		for (String s : serverLog) {
			log += "[" + s + "]\n";
		}
		return log;
	}
	
	private String getTimeStamp() {
		// Create a time stamp
		String timeStamp = new SimpleDateFormat("hh:mm:ss aaa").format(Calendar.getInstance().getTime());
		return timeStamp;
	}
}