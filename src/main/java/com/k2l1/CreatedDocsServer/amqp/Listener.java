package com.k2l1.CreatedDocsServer.amqp;

import java.nio.charset.StandardCharsets;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.k2l1.CreatedDocsServer.messageBodies.Authentication;
import com.k2l1.CreatedDocsServer.messageBodies.AuthenticationResult;
import com.k2l1.CreatedDocsServer.model.service.AuthenticationService;

@Component
public class Listener {
	@Autowired
	ConnectionFactory connectionFactory;
	
	@Autowired
	AuthenticationService authenticationService;
	

	@RabbitListener(
			containerFactory = "simpleJsonListenerContainerFactory",
			bindings = @QueueBinding(
			value = @Queue(value = "created-docs-server-q", durable = "true"),
			exchange = @Exchange(value = "created-docs.direct", ignoreDeclarationExceptions = "true"),
			key = "app.auth"))
	public void processAppAuth(Authentication authentication, Message message) {
		// String msg 로 하면 message body가 출력된다.
		System.out.println(authentication);
		
		AuthenticationResult result = new AuthenticationResult();
		result.setResultCode(AuthenticationResult.ResultCode.ERROR);
		result.setMessage("Invalid Authentication.type");
		
		switch (authentication.getType()) {
			case Authentication.Type.NORMAL :
				result = authenticationService.authenticateNormal(authentication);
				break;
			case Authentication.Type.ENFORCED :
				result = authenticationService.authenticateEnforced(authentication);
				break;
			case Authentication.Type.ACTIVATE_NEW :
				result = authenticationService.authenticateAndActivateNew(authentication);
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
	

	@Bean
	public SimpleRabbitListenerContainerFactory simpleJsonListenerContainerFactory() {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
		factory.setMessageConverter(messageConverter);
		factory.setReceiveTimeout(10L);
		return factory;
	}
	
	
	
}
