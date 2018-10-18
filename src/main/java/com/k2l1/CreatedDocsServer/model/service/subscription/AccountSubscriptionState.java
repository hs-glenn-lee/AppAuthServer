package com.k2l1.CreatedDocsServer.model.service.subscription;

public enum AccountSubscriptionState {
	
	AVAILABLE("AVAILABLE"),
	UNAVAILABLE("UNAVAILABLE");
	
	private String stateValue;
	
	AccountSubscriptionState(String stateValue) {
		this.stateValue = stateValue;
	}
	
	public String getStateValue() {
		return stateValue;
	}
}
