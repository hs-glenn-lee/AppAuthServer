package com.k2l1.CreatedDocsServer.messageTypes;

public class AuthenticationResult {
	
	public static class ResultCode {
		public static final String AUTHORIZED = "AUTHORIZED";
		public static final String UNAUHORIZED = "UNAUHORIZED";
		public static final String ERROR = "ERROR";
		public static final String NEED_TO_ACTIVATE_NEW = "NEED_TO_ACTIVATE_NEW";
		public static final String DUPLICATED = "DUPLICATED";
	}
	
	private String resultCode;
	private ActivatedSubscription activatedSubscription;
	private String message;
	
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
