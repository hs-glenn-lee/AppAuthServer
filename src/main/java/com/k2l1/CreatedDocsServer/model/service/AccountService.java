package com.k2l1.CreatedDocsServer.model.service;

import java.util.Optional;

import com.k2l1.CreatedDocsServer.model.jpa.entities.Account;

public interface AccountService {
	public Account findAccount(String username, String password);
}
