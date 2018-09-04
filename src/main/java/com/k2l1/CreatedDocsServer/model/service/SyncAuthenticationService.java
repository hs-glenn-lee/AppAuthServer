package com.k2l1.CreatedDocsServer.model.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.k2l1.CreatedDocsServer.messageTypes.ActivatedSubscription;
import com.k2l1.CreatedDocsServer.messageTypes.Authentication;
import com.k2l1.CreatedDocsServer.messageTypes.AuthenticationResult;
import com.k2l1.CreatedDocsServer.messageTypes.Unauthorization;
import com.k2l1.CreatedDocsServer.model.jpa.entities.Account;
import com.k2l1.CreatedDocsServer.model.jpa.entities.Subscription;
import com.k2l1.CreatedDocsServer.model.jpa.repos.AccountRepo;
import com.k2l1.CreatedDocsServer.model.jpa.repos.SubscriptionRepo;
import com.k2l1.CreatedDocsServer.model.redis.AppConnection;

@Service("authenticationService")
public class SyncAuthenticationService implements AuthenticationService{
	
	Logger logger = LoggerFactory.getLogger(SyncAuthenticationService.class);
	
	@Autowired
	SubscriptionRepo subscriptionRepo;
	
	@Autowired
	AccountRepo accountRepo;
	
	@Autowired
	ConnectionFactory connectionFactory;
	
	@Autowired
	AppConnectionService appConnectionService;
	
	
	@Override
	public AuthenticationResult authenticateNormal(Authentication authentication) {
		
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
			
			if(activated.isEmpty()) {
				if(permitted.isEmpty()) {
					AuthenticationResult ret = new AuthenticationResult();
					ret.setResultCode(AuthenticationResult.ResultCode.UNAUHORIZED);
					return ret;
				}else {
					AuthenticationResult ret = new AuthenticationResult();
					ret.setResultCode(AuthenticationResult.ResultCode.NEED_TO_ACTIVATE_NEW);
					return ret;
				}
			}else {
				AppConnection appConnection = new AppConnection(tryingAccount, authentication);
				AuthenticationResult authResult = new AuthenticationResult();
				Optional<AppConnection> existed = appConnectionService.get(appConnection.getAccountId());
				if(existed.isPresent()) {
					if(authentication.getClientId().equals(existed.get().getClientId())) {
						appConnectionService.set(appConnection);
						authResult.setResultCode(AuthenticationResult.ResultCode.AUTHORIZED);
						ActivatedSubscription activatedSubs = new ActivatedSubscription(activated.get(0));
						authResult.setActivatedSubscription(activatedSubs);
					}else {
						authResult.setResultCode(AuthenticationResult.ResultCode.DUPLICATED);
					}
				}else {
					appConnectionService.set(appConnection);
					authResult.setResultCode(AuthenticationResult.ResultCode.AUTHORIZED);
					ActivatedSubscription activatedSubs = new ActivatedSubscription(activated.get(0));
					authResult.setActivatedSubscription(activatedSubs);
				}

				return authResult;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			AuthenticationResult ret = new AuthenticationResult();
			ret.setResultCode(AuthenticationResult.ResultCode.ERROR);
			return ret;
			
		}
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
		return subscriptionRepo.findImportantSubscriptionsOf(account);
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
	}

	@Override
	public AuthenticationResult authenticateAndActivateNew(Authentication authentication) {
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
			
			AuthenticationResult authResult = new AuthenticationResult();
			if(permitted.isEmpty()) {
				authResult.setResultCode(AuthenticationResult.ResultCode.ERROR);
				return authResult;
			}else {
				Subscription newActivated = this.activate(permitted.get(0));
				
				AppConnection appConnection = new AppConnection(tryingAccount, authentication);
				appConnectionService.set(appConnection);
				
				authResult.setResultCode(AuthenticationResult.ResultCode.AUTHORIZED);
				ActivatedSubscription activatedSubs = new ActivatedSubscription(newActivated);
				authResult.setActivatedSubscription(activatedSubs);
				return authResult;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			AuthenticationResult ret = new AuthenticationResult();
			ret.setResultCode(AuthenticationResult.ResultCode.ERROR);
			return ret;
			
		}
	}

	@Override
	public AuthenticationResult authenticateEnforced(Authentication authentication) {
		try {
			Account tryingAccount = findAccount(authentication.getUsername(), authentication.getPassword());
			if(tryingAccount == null) { throw new IllegalStateException("해당 사용자를 찾을 수 없습니다."); }
			
			List<Subscription> importants = getImportantSubscriptions(tryingAccount);

			Subscription activated = null;
			
			for(Subscription sub : importants) {
				switch(sub.getState()) {
				case Subscription.State.ACTIVATED:
					activated = sub;
					break;
				}
			}
			
			AuthenticationResult authResult = new AuthenticationResult();
			if(activated == null) {
				authResult.setResultCode(AuthenticationResult.ResultCode.ERROR);
				return authResult;
			}else {
				AppConnection appConnection = new AppConnection(tryingAccount, authentication);
				
				Optional<AppConnection> removed = appConnectionService.remove(appConnection.getAccountId());
				appConnectionService.set(appConnection);
				if(removed.isPresent()) {
					unauthorizeClient(removed.get().getClientId());
				}

				authResult.setResultCode(AuthenticationResult.ResultCode.AUTHORIZED);
				ActivatedSubscription activatedSubs = new ActivatedSubscription(activated);
				authResult.setActivatedSubscription(activatedSubs);
				return authResult;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			AuthenticationResult ret = new AuthenticationResult();
			ret.setResultCode(AuthenticationResult.ResultCode.ERROR);
			return ret;
		}
	}

	@Override
	public void authenticate(Authentication authentication, Message message) {
		AuthenticationResult result = null;
		switch (authentication.getType()) {
			case Authentication.Type.NORMAL :
				result = authenticateNormal(authentication);
				break;
			case Authentication.Type.ENFORCED :
				result = authenticateEnforced(authentication);
				break;
			case Authentication.Type.ACTIVATE_NEW :
				result = authenticateAndActivateNew(authentication);
				break;
			default:
				result = new AuthenticationResult();
				result.setResultCode(AuthenticationResult.ResultCode.ERROR);
				result.setMessage("Authencation.type is not proper.");
				break;
		}
		
		/*reply*/
		MessageProperties reponseProperties = new MessageProperties();
		reponseProperties.setContentType("application/json");
		reponseProperties.setContentEncoding(StandardCharsets.UTF_8.name());
		reponseProperties.setCorrelationId(message.getMessageProperties().getCorrelationId());
		
		RabbitTemplate rabbitTemplate = new RabbitTemplate();
		rabbitTemplate.setConnectionFactory(connectionFactory);
		rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
		rabbitTemplate.convertAndSend(message.getMessageProperties().getReplyTo(), result);
	}
	
	
	public void unauthorizeClient (String clientId) {
		MessageProperties reponseProperties = new MessageProperties();
		reponseProperties.setContentType("application/json");
		reponseProperties.setContentEncoding(StandardCharsets.UTF_8.name());
		
		RabbitTemplate rabbitTemplate = new RabbitTemplate();
		rabbitTemplate.setConnectionFactory(connectionFactory);
		rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
		
		String routingKey = String.format("cd.client.%s.unauth", clientId);
		rabbitTemplate.convertAndSend(routingKey, new Unauthorization());
	}

}
