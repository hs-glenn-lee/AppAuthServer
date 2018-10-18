package com.k2l1.CreatedDocsServer.amqp;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.k2l1.CreatedDocsServer.messages.AuthenticationMessage;
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
			value = @Queue(value = "cd-server-auth-q", durable = "true", autoDelete = "false"),
			exchange = @Exchange(value = "created-docs.direct", ignoreDeclarationExceptions = "true"),
			arguments = @Argument(name = "BodyType", /*type="String",*/ value="Authentication.json"),
			key = "cd.server.auth"))
	public void processAppAuth(AuthenticationMessage authentication, Message message) {
		// String msg 로 하면 message body가 출력된다.
		System.out.println(authentication);
		
		authenticationService.authenticate(authentication, message);
		
		
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
