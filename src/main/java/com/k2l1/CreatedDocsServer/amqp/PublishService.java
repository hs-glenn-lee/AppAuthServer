package com.k2l1.CreatedDocsServer.amqp;

import java.nio.charset.StandardCharsets;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.k2l1.CreatedDocsServer.messageBodies.AuthenticationResult;

@Component
public class PublishService {
	
	@Autowired
	ConnectionFactory connectionFactory;
	
	public void replyOfAuthentication(Message message, Object messageBodies, AuthenticationResult authenticationResult) {
		MessageProperties reponseProperties = getDefaultMessageProperties();
		reponseProperties.setCorrelationId(message.getMessageProperties().getCorrelationId());
		
		RabbitTemplate rabbitTemplate =getDefaultRabbitTemplate();
		rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
		rabbitTemplate.convertAndSend(message.getMessageProperties().getReplyTo(), authenticationResult);
	}
	
	public void unauthorize(String clientId) {
		MessageProperties reponseProperties = new MessageProperties();
		reponseProperties.setContentType("application/json");
		reponseProperties.setContentEncoding(StandardCharsets.UTF_8.name());
		
		RabbitTemplate rabbitTemplate = new RabbitTemplate();
		rabbitTemplate.setConnectionFactory(connectionFactory);
		
	}
	
	public MessageProperties getDefaultMessageProperties() {
		MessageProperties reponseProperties = new MessageProperties();
		reponseProperties.setContentType("application/json");
		reponseProperties.setContentEncoding(StandardCharsets.UTF_8.name());
		return reponseProperties;
	}
	
	public RabbitTemplate getDefaultRabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate();
		rabbitTemplate.setConnectionFactory(connectionFactory);
		return rabbitTemplate;
	}
}
