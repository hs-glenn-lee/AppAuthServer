package com.k2l1.CreatedDocsServer.messageBodies;

public class AuthenticationResult {
	private String resultCode;
	private ActivatedSubscription activatedSubscription;
	
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public ActivatedSubscription getActivatedSubscription() {
		return activatedSubscription;
	}
	public void setActivatedSubscription(ActivatedSubscription activatedSubscription) {
		this.activatedSubscription = activatedSubscription;
	}
	
}
