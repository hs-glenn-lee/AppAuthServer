package com.k2l1.CreatedDocsServer.amqp.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RabbitMqHttpApi {

	@GET("/api/queues/{vhost}/{qname}")
	public Call<ResponseBody> getQueue(@Path("vhost")String vhost,	@Path("qname")String qname);

}
