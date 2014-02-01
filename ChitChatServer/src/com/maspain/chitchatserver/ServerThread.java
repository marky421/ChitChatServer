package com.maspain.chitchatserver;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread {
	
	private Server server;	// The Server that spawned us
	private DataInputStream din;
	private User user;
	private boolean chatting = false;
	
	public ServerThread(Server server, Socket socket) {
		// socket is the socket that our client used to connect to us
		
		this.server = server;
		setName("ServerThread  |  client @" + socket);
		user = new User(socket);
		
		try {
			din = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		chatting = true;
		
		start();
	}
	
	public void run() {
		String data;
		
		try {
			while (chatting) {
				// ...read the next message...
				data = din.readUTF();
				String s = "Sending: '" + data + "'";
				System.out.println(s);
				server.log(s);
				server.sendToAll(processMessage(data));
			}
		} catch (EOFException ie) {
			// Not necessary to report this exception
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			// The connection is closed for one reason or another, so have the server associated with it remove the connection
			server.removeConnection(user);
			chatting = false;
		}
	}
	
	private String processMessage(String message) {
		Packet incomingPacket = new Packet(message);
		Packet outgoingPacket;
		
		if (incomingPacket.getCommand().equals(Packet.CONNECT)) {
			user.setName(incomingPacket.getSender());
			server.users.add(user);
			outgoingPacket = new Packet(Packet.CONNECT, incomingPacket.getSender(), server.getUsersAsString());
		}
		else if (incomingPacket.getCommand().equals(Packet.DISCONNECT)) {
			server.users.remove(user);
			outgoingPacket = new Packet(Packet.DISCONNECT, incomingPacket.getSender(), server.getUsersAsString());
		}
		else if (incomingPacket.getCommand().equals(Packet.MESSAGE)) {
			outgoingPacket = new Packet(incomingPacket);
		}
		else if (incomingPacket.getCommand().equals(Packet.TYPING)) {
			if (incomingPacket.getMessage().equals("begin typing")) user.setTyping(true);
			else if (incomingPacket.getMessage().equals("end typing")) user.setTyping(false);
			outgoingPacket = new Packet(Packet.TYPING, incomingPacket.getSender(), server.getUsersAsString());
		}
		else {
			outgoingPacket = new Packet(incomingPacket);
		}
		
		return outgoingPacket.getData();
	}
}