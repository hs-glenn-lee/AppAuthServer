package com.k2l1.CreatedDocsServer.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.k2l1.CreatedDocsServer.model.jpa.entities.Account;
import com.k2l1.CreatedDocsServer.model.jpa.repos.AccountRepo;

@Service("accountService")
public class AccountServiceImpl implements AccountService{

	@Autowired
	AccountRepo accountRepo;
	
	@Override
	public Account findAccount(String username, String password) {
		Account finded = accountRepo.findByUsernameAndPassword(username, password);
		return finded;
	}

}
