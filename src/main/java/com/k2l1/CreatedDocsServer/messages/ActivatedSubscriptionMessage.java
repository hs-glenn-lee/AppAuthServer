package com.k2l1.CreatedDocsServer.messages;

import com.k2l1.CreatedDocsServer.model.jpa.entities.Subscription;

public class ActivatedSubscriptionMessage {
	private String id;
	private String activatedAt;
	
	public ActivatedSubscriptionMessage () {}
	public ActivatedSubscriptionMessage (Subscription subscription) {
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
