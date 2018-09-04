package com.k2l1.CreatedDocsServer.messageTypes;

import java.io.Serializable;

public class Unauthorization implements Serializable{
	
	private static final long serialVersionUID = -1812911427008169986L;

	public static final String ENFORCED_UNAUTH_CODE = "ENFORCED_UNAUTH";
	
	private String code;
	private String message;
	
	public Unauthorization () {
		this.code = ENFORCED_UNAUTH_CODE;
		this.message = "다른 곳에서 로그인  하여  로그아웃 되었습니다.";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
