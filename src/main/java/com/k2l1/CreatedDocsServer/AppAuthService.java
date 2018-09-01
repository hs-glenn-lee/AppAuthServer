package com.k2l1.CreatedDocsServer;

import java.io.UnsupportedEncodingException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
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

@Component
public class AppAuthService {
	@Autowired
	ConnectionFactory connectionFactory;
	

	@RabbitListener(
			containerFactory = "simpleJsonListenerContainerFactory",
			bindings = @QueueBinding(
			value = @Queue(value = "created-docs-server-q", durable = "true"),
			exchange = @Exchange(value = "created-docs.direct", ignoreDeclarationExceptions = "true"),
			key = "app.auth"))
	public void processAppAuth(AppAuthMessage message) {
		// String msg 로 하면 message body가 출력된다.
		System.out.println(message);

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
