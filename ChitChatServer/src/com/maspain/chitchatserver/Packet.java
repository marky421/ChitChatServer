package com.maspain.chitchatserver;

public class Packet {
	
	private String command;
	private String sender;
	private String message;
	private String data;
	
	public static final String MESSAGE = "MESSAGE";
	public static final String CONNECT = "CONNECT";
	public static final String DISCONNECT = "DISCONNECT";
	public static final String TYPING = "TYPING";
	
	private final String delimiter = "||";
	
	public Packet(Packet packet) {
		this.command = packet.getCommand();
		this.sender = packet.getSender();
		this.message = packet.getMessage();
		this.data = packet.getData();
	}
	
	public Packet(String data) {
		this.data = data;
		parse();
	}
	
	public Packet(String command, String sender, String message) {
		if (!(command.equals(MESSAGE) || command.equals(CONNECT) || command.equals(DISCONNECT) || command.equals(TYPING))) {
			System.out.println("Invalid command");
			return;
		}
		this.command = command;
		this.sender = sender;
		this.message = message;
		data = command + delimiter + sender + delimiter + message;
	}
	
	// Designed to handle a 3-segment data stream that is separated twice by the delimiter
	private void parse() {
		
		int lastPos = 0;
		int segment = 0;
		char[] cursor = new char[delimiter.length()];
		
		for (int i = 0; i <= data.length() - delimiter.length(); i++) {
			
			if (segment == 2) {
				message = data.substring(lastPos);
				return;
			}
			
			// reset the cursor
			for (int j = 0; j < delimiter.length(); j++) {
				cursor[j] = data.charAt(i + j);
			}
			
			if (String.copyValueOf(cursor).equals(delimiter)) {
				
				switch (segment) {
					case 0:
						command = data.substring(lastPos, i);
						break;
					case 1:
						sender = data.substring(lastPos, i);
						break;
					default:
						break;
				}
				
				lastPos = i + delimiter.length();
				segment++;
			}
		}
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getData() {
		return data;
	}
}