package com.k2l1.CreatedDocsServer.model.service.subscription;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.k2l1.CreatedDocsServer.model.jpa.entities.Account;
import com.k2l1.CreatedDocsServer.model.jpa.entities.Subscription;
import com.k2l1.CreatedDocsServer.model.jpa.repos.SubscriptionRepo;

@Service("subscriptionService")
public class SubscriptionServiceImpl implements SubscriptionService{
	
	
	@Autowired
	SubscriptionRepo subscriptionRepo;
	
	@Override
	public AffectiveSubscriptions getAffectiveSubscriptions(Account account) {
		List<Subscription> finded = subscriptionRepo.findAffectiveSubscriptionsOf(account);
		AffectiveSubscriptions ret = new AffectiveSubscriptions(finded);
		return ret;
	}

	@Override
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

	private void validateActivatingSubscription(Optional<Subscription> opt) {
		if(!opt.isPresent())
			throw new IllegalStateException("해당하는 구독이 없습니다.");
		Subscription target = opt.get();
		if(!target.getState().equals(Subscription.State.PERMITTED)) {
			throw new IllegalStateException("활성화할 수 없는 상태의 구독입니다.");
		}
	}
	
	private Date calculateExpireDate(Subscription subscription, Date start) {
		Long amount = subscription.getPeriodAmount();
		/*String unit = subscription.getPeriodUnit();*/
		Date expireAt = DateUtils.addMonths(start, amount.intValue());
		DateUtils.addDays(expireAt, 1);
		DateUtils.addSeconds(expireAt, -1);
		return expireAt;
	}

}
