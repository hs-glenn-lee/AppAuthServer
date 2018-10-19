package com.k2l1.CreatedDocsServer.model.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.k2l1.CreatedDocsServer.model.redis.AppConnection;
import com.k2l1.CreatedDocsServer.model.service.subscription.AccountSubscriptionState;
import com.k2l1.CreatedDocsServer.model.service.subscription.AffectiveSubscriptions;
import com.k2l1.CreatedDocsServer.model.service.subscription.SubscriptionService;

@Service("authenticationService")
public class AuthenticationServiceImpl implements AuthenticationService{
	
	Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

	@Autowired
	SubscriptionService subscriptionService;
	
	@Autowired
	ConnectionFactory connectionFactory;
	
	@Autowired
	AppConnectionService appConnectionService;
	
	@Autowired
	AccountService accountService;
	

	private AuthenticationResultMessage authenticateNormal(AuthenticationMessage authentication) {
		try {
			Account tryingAccount = accountService.findAccount(authentication.getUsername(), authentication.getPassword());
			if(tryingAccount == null) { throw new IllegalStateException("해당 사용자를 찾을 수 없습니다."); }
			
			AffectiveSubscriptions affectives = subscriptionService.getAffectiveSubscriptions(tryingAccount);

			if(affectives.getAccountSubscriptionState() == AccountSubscriptionState.UNAVAILABLE) {
				return new AuthenticationResultMessage(AuthenticationResultMessage.ResultCode.UNAUHORIZED);
			}else {
				if(!affectives.hasActivated()) {
					return new AuthenticationResultMessage(AuthenticationResultMessage.ResultCode.NEED_TO_ACTIVATE_NEW);
				}else {
					AppConnection appConnection = new AppConnection(tryingAccount, authentication);
					Optional<AppConnection> existed = appConnectionService.get(appConnection.getAccountId());
					if(existed.isPresent()) {
						if(!authentication.getClientId().equals(existed.get().getClientId())) {
							unauthorizeClient(existed.get().getClientId());
						}
						appConnectionService.set(appConnection);
						return new AuthenticationResultMessage(AuthenticationResultMessage.ResultCode.AUTHORIZED, affectives.getActivated());
					}else {
						return new AuthenticationResultMessage(AuthenticationResultMessage.ResultCode.AUTHORIZED, affectives.getActivated());
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			return new AuthenticationResultMessage(AuthenticationResultMessage.ResultCode.ERROR, e.getMessage());
		}
	}

	private AuthenticationResultMessage authenticateAndActivateNew(AuthenticationMessage authentication) {
		try {
			Account tryingAccount = accountService.findAccount(authentication.getUsername(), authentication.getPassword());
			if(tryingAccount == null) { throw new IllegalStateException("해당 사용자를 찾을 수 없습니다."); }
			
			AffectiveSubscriptions affectives = subscriptionService.getAffectiveSubscriptions(tryingAccount);
			if(affectives.getAccountSubscriptionState() == AccountSubscriptionState.UNAVAILABLE) {
				return new AuthenticationResultMessage(AuthenticationResultMessage.ResultCode.ERROR, "활성화 가능한 구독이 없습니다.");
			}else {
				Subscription newActivated = subscriptionService.activate(affectives.getPermitted());
				AppConnection appConnection = new AppConnection(tryingAccount, authentication);
				appConnectionService.set(appConnection);
				return new AuthenticationResultMessage(AuthenticationResultMessage.ResultCode.AUTHORIZED, newActivated);
			}
		} catch(Exception e) {
			e.printStackTrace();
			return new AuthenticationResultMessage(AuthenticationResultMessage.ResultCode.ERROR, e.getMessage());
		}
	}

	
/*	private AuthenticationResultMessage authenticateEnforcedly(AuthenticationMessage authentication) {
		try {
			Account tryingAccount = accountService.findAccount(authentication.getUsername(), authentication.getPassword());
			if(tryingAccount == null) { throw new IllegalStateException("해당 사용자를 찾을 수 없습니다."); }
			
			AffectiveSubscriptions affectives = subscriptionService.getAffectiveSubscriptions(tryingAccount);
			if(!affectives.hasActivated()) {
				return new AuthenticationResultMessage(AuthenticationResultMessage.ResultCode.ERROR, "활성화된 구독이 없습니다.");
			}else {
				AppConnection appConnection = new AppConnection(tryingAccount, authentication);
				
				Optional<AppConnection> removed = appConnectionService.remove(appConnection.getAccountId());
				appConnectionService.set(appConnection);
				if(removed.isPresent()) {
					unauthorizeClient(removed.get().getClientId());
				}
				return new AuthenticationResultMessage(AuthenticationResultMessage.ResultCode.AUTHORIZED, affectives.getActivated());
			}
			
		} catch(Exception e) {
			return new AuthenticationResultMessage(AuthenticationResultMessage.ResultCode.ERROR, e.getMessage());
		}
	}*/

	@Override
	public void authenticate(AuthenticationMessage authentication, Message message) {
		AuthenticationResultMessage result = null;
		switch (authentication.getType()) {
			case AuthenticationMessage.Type.NORMAL :
				result = authenticateNormal(authentication);
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
		
		//TODO default exchnage로 보내는 방식으로 바꿔야한다.
		String routingKey = String.format("cd.client.%s.unauth", clientId);
		rabbitTemplate.convertAndSend(routingKey, new UnauthorizationMessage());
	}

}
