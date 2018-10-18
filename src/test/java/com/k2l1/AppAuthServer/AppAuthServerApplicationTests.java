package com.k2l1.AppAuthServer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.k2l1.CreatedDocsServer.CreatedDocsServer;
import com.k2l1.CreatedDocsServer.amqp.api.RabbitMqApi;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CreatedDocsServer.class)
public class AppAuthServerApplicationTests {
	
	Logger logger = LoggerFactory.getLogger(AppAuthServerApplicationTests.class);
	
	
	@Autowired
	RabbitMqApi rabbitMqApi;
	
	@Autowired
	ApplicationContext ac;
	
	@Before
	public void setup() throws Exception {
		
	}
	
	@Test
	public void contextLoads() {
		boolean ret = rabbitMqApi.isQueueExsits("created-docs-server-q");
		logger.debug("ret"+ret);
	}

}
