package com.k2l1.CreatedDocsServer.model.service;

import com.k2l1.CreatedDocsServer.messageBodies.Authentication;
import com.k2l1.CreatedDocsServer.messageBodies.AuthenticationResult;


public interface AuthenticationService {
	public AuthenticationResult authenticateNormal (Authentication authentication);
	public AuthenticationResult authenticateAndActivateNew (Authentication authentication);
	public AuthenticationResult authenticateEnforced (Authentication authentication);
}
