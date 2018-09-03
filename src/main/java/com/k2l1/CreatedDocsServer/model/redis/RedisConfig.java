package com.k2l1.CreatedDocsServer.model.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

public class RedisConfig {
	
	@Autowired
	JedisConnectionFactory jedisConnectinoFactory;
	
	@Bean
	public RedisTemplate<String, AppConnection> redisTemplate() {
		final RedisTemplate<String, AppConnection> template = new RedisTemplate<String, AppConnection>();
		template.setConnectionFactory(jedisConnectinoFactory);
		template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
		return template;
	}
}
