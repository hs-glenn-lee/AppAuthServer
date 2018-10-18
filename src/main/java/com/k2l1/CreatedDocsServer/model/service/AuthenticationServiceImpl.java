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

import com.k2l1.CreatedDocsServer.messages.ActivatedSubscriptionMessage;
import com.k2l1.CreatedDocsServer.messages.AuthenticationMessage;
import com.k2l1.CreatedDocsServer.messages.AuthenticationResultMessage;
import com.k2l1.CreatedDocsServer.messages.UnauthorizationMessage;
import com.k2l1.CreatedDocsServer.model.jpa.entities.Account;
import com.k2l1.CreatedDocsServer.model.jpa.entities.Subscription;
import com.k2l1.CreatedDocsServer.model.jpa.repos.AccountRepo;
import com.k2l1.CreatedDocsServer.model.jpa.repos.SubscriptionRepo;
import com.k2l1.CreatedDocsServer.model.redis.AppConnection;

@Service("authenticationService")
public class AuthenticationServiceImpl implements AuthenticationService{
	
	Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
	
	@Autowired
	SubscriptionRepo subscriptionRepo;
	
	@Autowired
	AccountRepo accountRepo;
	
	@Autowired
	ConnectionFactory connectionFactory;
	
	@Autowired
	AppConnectionService appConnectionService;
	
	
	@Override
	public AuthenticationResultMessage authenticateNormal(AuthenticationMessage authentication) {
		
		try {
			Account tryingAccount = findAccount(authentication.getUsername(), authentication.getPassword());
			if(tryingAccount == null) { throw new IllegalStateException("해당 사용자를 찾을 수 없습니다."); }
			
			List<Subscription> importants = getAffectiveSubscriptions(tryingAccount);
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
					AuthenticationResultMessage ret = new AuthenticationResultMessage();
					ret.setResultCode(AuthenticationResultMessage.ResultCode.UNAUHORIZED);
					return ret;
				}else {
					AuthenticationResultMessage ret = new AuthenticationResultMessage();
					ret.setResultCode(AuthenticationResultMessage.ResultCode.NEED_TO_ACTIVATE_NEW);
					return ret;
				}
			}else {
				AppConnection appConnection = new AppConnection(tryingAccount, authentication);
				AuthenticationResultMessage authResult = new AuthenticationResultMessage();
				Optional<AppConnection> existed = appConnectionService.get(appConnection.getAccountId());
				if(existed.isPresent()) {
					if(authentication.getClientId().equals(existed.get().getClientId())) {
						appConnectionService.set(appConnection);
						authResult.setResultCode(AuthenticationResultMessage.ResultCode.AUTHORIZED);
						ActivatedSubscriptionMessage activatedSubs = new ActivatedSubscriptionMessage(activated.get(0));
						authResult.setActivatedSubscription(activatedSubs);
					}else {
						authResult.setResultCode(AuthenticationResultMessage.ResultCode.DUPLICATED);
					}
				}else {
					appConnectionService.set(appConnection);
					authResult.setResultCode(AuthenticationResultMessage.ResultCode.AUTHORIZED);
					ActivatedSubscriptionMessage activatedSubs = new ActivatedSubscriptionMessage(activated.get(0));
					authResult.setActivatedSubscription(activatedSubs);
				}

				return authResult;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			AuthenticationResultMessage ret = new AuthenticationResultMessage();
			ret.setResultCode(AuthenticationResultMessage.ResultCode.ERROR);
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
	
	private List<Subscription> getAffectiveSubscriptions(Account account) {
		return subscriptionRepo.findAffectiveSubscriptionsOf(account);
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
	public AuthenticationResultMessage authenticateAndActivateNew(AuthenticationMessage authentication) {
		try {
			Account tryingAccount = findAccount(authentication.getUsername(), authentication.getPassword());
			if(tryingAccount == null) { throw new IllegalStateException("해당 사용자를 찾을 수 없습니다."); }
			
			List<Subscription> importants = getAffectiveSubscriptions(tryingAccount);
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
			
			AuthenticationResultMessage authResult = new AuthenticationResultMessage();
			if(permitted.isEmpty()) {
				authResult.setResultCode(AuthenticationResultMessage.ResultCode.ERROR);
				return authResult;
			}else {
				Subscription newActivated = this.activate(permitted.get(0));
				
				AppConnection appConnection = new AppConnection(tryingAccount, authentication);
				appConnectionService.set(appConnection);
				
				authResult.setResultCode(AuthenticationResultMessage.ResultCode.AUTHORIZED);
				ActivatedSubscriptionMessage activatedSubs = new ActivatedSubscriptionMessage(newActivated);
				authResult.setActivatedSubscription(activatedSubs);
				return authResult;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			AuthenticationResultMessage ret = new AuthenticationResultMessage();
			ret.setResultCode(AuthenticationResultMessage.ResultCode.ERROR);
			return ret;
			
		}
	}

	@Override
	public AuthenticationResultMessage authenticateEnforcedly(AuthenticationMessage authentication) {
		try {
			Account tryingAccount = findAccount(authentication.getUsername(), authentication.getPassword());
			if(tryingAccount == null) { throw new IllegalStateException("해당 사용자를 찾을 수 없습니다."); }
			
			List<Subscription> importants = getAffectiveSubscriptions(tryingAccount);

			Subscription activated = null;
			
			for(Subscription sub : importants) {
				switch(sub.getState()) {
				case Subscription.State.ACTIVATED:
					activated = sub;
					break;
				}
			}
			
			AuthenticationResultMessage authResult = new AuthenticationResultMessage();
			if(activated == null) {
				authResult.setResultCode(AuthenticationResultMessage.ResultCode.ERROR);
				return authResult;
			}else {
				AppConnection appConnection = new AppConnection(tryingAccount, authentication);
				
				Optional<AppConnection> removed = appConnectionService.remove(appConnection.getAccountId());
				appConnectionService.set(appConnection);
				if(removed.isPresent()) {
					unauthorizeClient(removed.get().getClientId());
				}

				authResult.setResultCode(AuthenticationResultMessage.ResultCode.AUTHORIZED);
				ActivatedSubscriptionMessage activatedSubs = new ActivatedSubscriptionMessage(activated);
				authResult.setActivatedSubscription(activatedSubs);
				return authResult;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			AuthenticationResultMessage ret = new AuthenticationResultMessage();
			ret.setResultCode(AuthenticationResultMessage.ResultCode.ERROR);
			return ret;
		}
	}

	@Override
	public void authenticate(AuthenticationMessage authentication, Message message) {
		AuthenticationResultMessage result = null;
		switch (authentication.getType()) {
			case AuthenticationMessage.Type.NORMAL :
				result = authenticateNormal(authentication);
				break;
			case AuthenticationMessage.Type.ENFORCED :
				result = authenticateEnforcedly(authentication);
				break;
			case AuthenticationMessage.Type.ACTIVATE_NEW :
				result = authenticateAndActivateNew(authentication);
				break;
			default:
				result = new AuthenticationResultMessage();
				result.setResultCode(AuthenticationResultMessage.ResultCode.ERROR);
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
	
	@Override
	public void unauthorizeClient (String clientId) {
		MessageProperties reponseProperties = new MessageProperties();
		reponseProperties.setContentType("application/json");
		reponseProperties.setContentEncoding(StandardCharsets.UTF_8.name());
		
		RabbitTemplate rabbitTemplate = new RabbitTemplate();
		rabbitTemplate.setConnectionFactory(connectionFactory);
		rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
		
		String routingKey = String.format("cd.client.%s.unauth", clientId);
		rabbitTemplate.convertAndSend(routingKey, new UnauthorizationMessage());
	}

}
