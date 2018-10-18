package com.k2l1.CreatedDocsServer.model.service.subscription;

import static org.mockito.Mockito.ignoreStubs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2l1.CreatedDocsServer.model.jpa.entities.Subscription;

public class AffectiveSubscriptions {
	
	Logger logger = LoggerFactory.getLogger(AffectiveSubscriptions.class);
	
	private Subscription requested;
	private Subscription activated;
	private Subscription permitted;
	
	public AffectiveSubscriptions(List<Subscription> subscriptions) {
		for(Subscription sub : subscriptions) {
			switch(sub.getState()) {
				case Subscription.State.ACTIVATED:
					if(activated !=null) { throw new IllegalArgumentException("1개 이상의 활성화 구독." + activated); }
					activated = sub;
					break;
				case Subscription.State.PERMITTED:
					if(permitted !=null) { throw new IllegalArgumentException("1개 이상의 승인 구독." + activated); }
					permitted = sub;
					break;
				case Subscription.State.REQUESTED:
					if(requested !=null) { throw new IllegalArgumentException("1개 이상의 신청 구독." + activated); }
					requested = sub;
					break;
			}
		}
	}
	
	public boolean hasActivated() {
		if(activated != null)
			return true;
		return false;
	}
	
	public boolean hasPermitted() {
		if(permitted != null)
			return true;
		return false;
	}
	
	public boolean hasRequested() {
		if(permitted != null)
			return true;
		return false;
	}
	
	public Subscription getActivated () {
		return activated;
	}
	public Subscription getPermitted () {
		return permitted;
	}
	public Subscription getRequested () {
		return requested;
	}
}
