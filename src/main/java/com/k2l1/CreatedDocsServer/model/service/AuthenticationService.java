package com.k2l1.CreatedDocsServer.model.service;

import com.k2l1.CreatedDocsServer.messageBodies.Authentication;
import com.k2l1.CreatedDocsServer.messageBodies.AuthenticationResult;


public interface AuthenticationService {
	public AuthenticationResult authenticate (Authentication authenticate);
}
