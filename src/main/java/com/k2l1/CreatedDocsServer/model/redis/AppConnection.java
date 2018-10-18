package com.k2l1.CreatedDocsServer.model.redis;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.k2l1.CreatedDocsServer.messages.AuthenticationMessage;
import com.k2l1.CreatedDocsServer.model.jpa.entities.Account;

@RedisHash("appConnection")
public class AppConnection implements Serializable{
	private static final long serialVersionUID = 6794782318797332870L;
	
	@Id private Long accountId;
	private String clientId;
	private Date createdAt;
	
	public AppConnection() {}
	public AppConnection(Account account, AuthenticationMessage authentication) {
		this.accountId = account.getId();
		this.clientId = authentication.getClientId();
	}
	
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
