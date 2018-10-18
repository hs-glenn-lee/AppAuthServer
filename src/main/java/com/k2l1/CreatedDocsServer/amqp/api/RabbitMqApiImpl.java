package com.k2l1.CreatedDocsServer.amqp.api;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

@Service("rabbitMqApi")
public class RabbitMqApiImpl implements RabbitMqApi{
	
	@Autowired
	ConnectionFactory connectionFactory;
	
	@Override
	public boolean isQueueExsits(String queueName) {
		String host = connectionFactory.getHost();
		String vhost = connectionFactory.getVirtualHost();
		String managerPort = "15672";
		
		OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://"+host+":"+managerPort)
				.client(httpClient.build())
				.build();
		
		RabbitMqHttpApi api = retrofit.create(RabbitMqHttpApi.class);
		Call<ResponseBody> res = api.getQueue(vhost, queueName);
		try {
			Response<ResponseBody> rs = res.execute();
			String resultJson = rs.body().string();
			Gson gson = new Gson();
			Type type = new TypeToken<Map<String, String>>(){}.getType();
			Map<String, String> jsonMap = gson.fromJson(resultJson, type);
			boolean hasErrorKey = jsonMap.containsKey("error");
			if(hasErrorKey) {
				return false;
			}else {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
