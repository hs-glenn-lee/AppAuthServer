package com.k2l1.CreatedDocsServer.messageBodies;

public class ActivatedSubscription {
	private String id;
	private String activatedAt;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getActivatedAt() {
		return activatedAt;
	}
	public void setActivatedAt(String activatedAt) {
		this.activatedAt = activatedAt;
	}
}
