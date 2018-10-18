package com.k2l1.CreatedDocsServer.messages;

public class AuthenticationMessage {
	
	public static class Type {
		public static final String NORMAL = "NORMAL";
		public static final String ACTIVATE_NEW = "ACTIVATE_NEW";
		public static final String ENFORCED = "ENFORCED";
	}
	
	private String type;
	private String username;
	private String password;
	private String clientId;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
