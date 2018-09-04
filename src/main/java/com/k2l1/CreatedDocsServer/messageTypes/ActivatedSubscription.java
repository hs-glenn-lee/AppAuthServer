package com.k2l1.CreatedDocsServer.messageTypes;

import com.k2l1.CreatedDocsServer.model.jpa.entities.Subscription;

public class ActivatedSubscription {
	private String id;
	private String activatedAt;
	
	public ActivatedSubscription () {}
	public ActivatedSubscription (Subscription subscription) {
		this.id = subscription.getId();
		this.activatedAt = subscription.getActivatedAt().toString();
	}
	
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
