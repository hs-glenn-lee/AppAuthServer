package com.k2l1.CreatedDocsServer.model.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.k2l1.CreatedDocsServer.amqp.api.RabbitMqApi;
import com.k2l1.CreatedDocsServer.model.redis.AppConnection;
import com.k2l1.CreatedDocsServer.model.redis.AppConnectionRepo;

@Service("connectionService")
public class AppConnectionServiceImpl implements AppConnectionService{
	
	@Autowired
	AppConnectionRepo appConnectionRepo;
	
	@Autowired
	RabbitMqApi rabbitMqApi;
	
	@Override
	public Optional<AppConnection> get(Long accountId) {
		return appConnectionRepo.findById(accountId);
	}

	@Override
	public void set(AppConnection appConnection) {
		System.out.println(appConnection);
		appConnectionRepo.save(appConnection);
	}

	@Override
	public Optional<AppConnection> remove(Long accountId) {
		Optional<AppConnection> opt = appConnectionRepo.findById(accountId);
		if(opt.isPresent()) {
			appConnectionRepo.delete(opt.get());
			return opt;
		}else {
			return opt;
		}
	}

	@Override
	public boolean isExists(AppConnection appConnection) {
		Optional<AppConnection> opt = this.get(appConnection.getAccountId());
		if(opt.isPresent()) {
			AppConnection  cur = opt.get();
			boolean isClinetQueueExsists = rabbitMqApi.isQueueExsits("client."+cur.getClientId()+".auth");
			return isClinetQueueExsists;
		}else {
			return false;
		}
	}

}
