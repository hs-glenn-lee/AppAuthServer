package com.k2l1.CreatedDocsServer.model.service;

import java.util.Optional;

import com.k2l1.CreatedDocsServer.model.redis.AppConnection;

public interface AppConnectionService {
	public Optional<AppConnection> get(Long accountId);
	public boolean isExists (AppConnection appConnection);
	public void set(AppConnection appConnection);
	public Optional<AppConnection> remove(Long accountId);
}
