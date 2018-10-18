package com.k2l1.CreatedDocsServer.messages;

import com.k2l1.CreatedDocsServer.model.jpa.entities.Subscription;

public class AuthenticationResultMessage {
	
	public static class ResultCode {
		public static final String AUTHORIZED = "AUTHORIZED";
		public static final String UNAUHORIZED = "UNAUHORIZED";
		public static final String ERROR = "ERROR";
		public static final String NEED_TO_ACTIVATE_NEW = "NEED_TO_ACTIVATE_NEW";
		public static final String DUPLICATED = "DUPLICATED";
	}
	
	private String resultCode;
	private ActivatedSubscriptionMessage activatedSubscription;
	private String message;
	
	public AuthenticationResultMessage () {}
	public AuthenticationResultMessage (String retCode) {
		this.resultCode = retCode;
	}
	public AuthenticationResultMessage (String retCode, String message) {
		this.resultCode = retCode;
		this.message = message;
	}
	public AuthenticationResultMessage (String retCode, Subscription activated) {
		this.resultCode = retCode;
		this.activatedSubscription = new ActivatedSubscriptionMessage(activated);
	}
	
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public ActivatedSubscriptionMessage getActivatedSubscription() {
		return activatedSubscription;
	}
	public void setActivatedSubscription(ActivatedSubscriptionMessage activatedSubscription) {
		this.activatedSubscription = activatedSubscription;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
