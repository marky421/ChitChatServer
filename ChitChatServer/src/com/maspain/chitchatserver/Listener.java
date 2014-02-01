package com.maspain.chitchatserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable {
	
	private ServerSocket serverSocket;
	private Server server;
	private Thread thread;
	private boolean listening = false;
	
	public Listener(ServerSocket serverSocket, Server server) {
		this.serverSocket = serverSocket;
		this.server = server;
		
		listening = true;
		
		thread = new Thread(this, "Listener  |  " + serverSocket);
		thread.start();
	}
	
	public void run() {
		Socket socket = null;
		
		while (listening) {
			
			// Grab the next incoming connection
			try {
				socket = serverSocket.accept();
			} catch (IOException ie) {
				ie.printStackTrace();
			}
			
			// Tell the world we've got it
			String s = "Connection from " + socket;
			System.out.println(s);
			server.log(s);
			
			// Create a DataOutputStream for writing data to the other side
			DataOutputStream dout;
			try {
				dout = new DataOutputStream(socket.getOutputStream());
				server.outputStreams.put(socket, dout);
			} catch (IOException ie) {
				ie.printStackTrace();
			}
			
			// Create a new thread for this connection, and then forget about it
			new ServerThread(server, socket);
		}
	}
}