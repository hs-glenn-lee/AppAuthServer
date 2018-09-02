package com.k2l1.CreatedDocsServer.model.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.k2l1.CreatedDocsServer.messageBodies.ActivatedSubscription;
import com.k2l1.CreatedDocsServer.messageBodies.Authentication;
import com.k2l1.CreatedDocsServer.messageBodies.AuthenticationResult;
import com.k2l1.CreatedDocsServer.model.entities.Account;
import com.k2l1.CreatedDocsServer.model.entities.Subscription;
import com.k2l1.CreatedDocsServer.model.repo.AccountRepo;
import com.k2l1.CreatedDocsServer.model.repo.SubscriptionRepo;

@Service("authenticationService")
public class SyncAuthenticationService implements AuthenticationService{
	
	@Autowired
	SubscriptionRepo subscriptionRepo;
	
	@Autowired
	AccountRepo accountRepo;
	
	@Autowired
	ConnectionFactory connectionFactory;
	
	
	@Override
	public AuthenticationResult authenticate(Authentication authentication) {
		
		try {
			Account tryingAccount = findAccount(authentication.getUsername(), authentication.getPassword());
			if(tryingAccount == null) { throw new IllegalStateException("해당 사용자를 찾을 수 없습니다."); }
			
			List<Subscription> importants = getImportantSubscriptions(tryingAccount);
			List<Subscription> activated = new ArrayList<Subscription>();
			List<Subscription> permitted = new ArrayList<Subscription>();

			for(Subscription sub : importants) {
				switch(sub.getState()) {
				case Subscription.State.ACTIVATED:
					if(activated.size() > 1) { }
					activated.add(sub);
					break;
				case Subscription.State.PERMITTED:
					permitted.add(sub);
					break;
				}
			}
			
			if(!activated.isEmpty()) {
				
			}else {
				AuthenticationResult ret = new AuthenticationResult();
				ret.setResultCode("AUTHORIZED");
				ActivatedSubscription ret2 = new ActivatedSubscription();
				ret2.setActivatedAt(activated.get(0).getActivatedAt().toString());
				ret2.setId(activated.get(0).getId());
				ret.setActivatedSubscription(ret2);
				return ret;
			}
			
		} catch(Exception e) {
			System.out.println(e);
		}
		
		AuthenticationResult ret = new AuthenticationResult();
		ret.setResultCode("UNAUHTORIZED");
		return ret;
		
	}
	
	private Account findAccount(String username, String password) {
		List<Account> finded = accountRepo.findByUsernameAndPassword(username, password);
		if(finded.isEmpty()) {
			return null;
		}else {
			return finded.get(0);
		}
	}
	
	private List<Subscription> getImportantSubscriptions(Account account) {
		subscriptionRepo.findImportantSubscriptionsOf(account);
		return null;
	}
	
	
	public Subscription activate(Subscription subscription) {
		Optional<Subscription> opt = subscriptionRepo.findById(subscription.getId());
		validateActivatingSubscription(opt);
		
		Subscription target = opt.get();
		target.setState(Subscription.State.ACTIVATED);
		target.setActivatedAt(new Date());
		target.setExpireAt(calculateExpireDate(target, target.getActivatedAt()));
		subscriptionRepo.save(target);
		return target;
	}
	
	private Date calculateExpireDate(Subscription subscription, Date start) {
		Long amount = subscription.getPeriodAmount();
		/*String unit = subscription.getPeriodUnit();*/
		Date expireAt = DateUtils.addMonths(start, amount.intValue());
		DateUtils.addDays(expireAt, 1);
		DateUtils.addSeconds(expireAt, -1);
		return expireAt;
	}
	
	private void validateActivatingSubscription(Optional<Subscription> opt) {
		if(!opt.isPresent())
			throw new IllegalStateException("해당하는 구독이 없습니다.");
		Subscription target = opt.get();
		if(!target.getState().equals(Subscription.State.PERMITTED)) {
			throw new IllegalStateException("활성화할 수 없는 상태의 구독입니다.");
		}
		
		//TODO 이미 활성화 되어있는 구독이 있는지 확인
	}
}
