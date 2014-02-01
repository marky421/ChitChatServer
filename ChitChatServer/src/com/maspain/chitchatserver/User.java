package com.maspain.chitchatserver;

import java.net.Socket;
import java.util.UUID;

public class User {
	
	private String name;
	private UUID id;
	private Socket socket;
	private boolean typing = false;
	
	public User(Socket socket) {
		this.socket = socket;
		id = UUID.randomUUID();
	}
	
	public User(String name, Socket socket) {
		this.name = name;
		this.socket = socket;
		id = UUID.randomUUID();
	}
	
	public String getName() {
		return this.name;
	}
	
	public UUID getID() {
		return this.id;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public boolean istyping() {
		return this.typing;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setTyping(boolean typing) {
		this.typing = typing;
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		User other = (User) obj;
		if (this.id == null) {
			if (other.id != null) return false;
		}
		else if (!this.id.equals(other.id)) return false;
		return true;
	}
}
