package com.k2l1.CreatedDocsServer.model.service.subscription;

import java.util.List;

import com.k2l1.CreatedDocsServer.model.jpa.entities.Account;
import com.k2l1.CreatedDocsServer.model.jpa.entities.Subscription;

public interface SubscriptionService {
	public AffectiveSubscriptions getAffectiveSubscriptions(Account account);
	public Subscription activate(Subscription subscription);
	
}
