package com.k2l1.CreatedDocsServer.model.redis;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface AppConnectionRepo extends  CrudRepository<AppConnection, Long>{
	
/*	public AppConnection findByAccountId(Long accountId);
	
*/
	
}