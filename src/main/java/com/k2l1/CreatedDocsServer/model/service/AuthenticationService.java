package com.k2l1.CreatedDocsServer.model.service;

import org.springframework.amqp.core.Message;

import com.k2l1.CreatedDocsServer.messageTypes.Authentication;
import com.k2l1.CreatedDocsServer.messageTypes.AuthenticationResult;


public interface AuthenticationService {
	public void authenticate (Authentication authentication, Message message);
	AuthenticationResult authenticateNormal (Authentication authentication);
	AuthenticationResult authenticateAndActivateNew (Authentication authentication);
	AuthenticationResult authenticateEnforced (Authentication authentication);
}
