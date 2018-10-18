package com.k2l1.CreatedDocsServer.model.service;

import org.springframework.amqp.core.Message;

import com.k2l1.CreatedDocsServer.messages.AuthenticationMessage;
import com.k2l1.CreatedDocsServer.messages.AuthenticationResultMessage;


public interface AuthenticationService {
	public void authenticate (AuthenticationMessage authentication, Message message);
	public void unauthorizeClient (String clientId);
}
