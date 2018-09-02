package com.k2l1.CreatedDocsServer.model.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.k2l1.CreatedDocsServer.model.entities.Account;
import com.k2l1.CreatedDocsServer.model.entities.Subscription;

public interface SubscriptionRepo extends JpaRepository<Subscription, String>{
	
	@Query("SELECT subs FROM Subscription subs "
			+ "WHERE subs.subscriber = :account ")
	List<Subscription> findSubscriptionOf(@Param("account") Account account);
	
	@Query("SELECT subs FROM Subscription subs "
			+ "WHERE subs.subscriber = :account "
			+ "AND subs.state = '" + Subscription.State.ACTIVATED +"' ")
	List<Subscription> findActivatedSubscriptionOf(@Param("account") Account account);
	
	@Query("SELECT subs FROM Subscription subs "
			+ "WHERE subs.subscriber = :account "
			+ "AND subs.state = '" + Subscription.State.REQUESTED +"' ")
	List<Subscription> findRequestedSubscriptionOf(@Param("account") Account account);
	
	@Query("SELECT subs FROM Subscription subs "
			+ "WHERE subs.subscriber = :account "
			+ "AND subs.state = '" + Subscription.State.PERMITTED +"' ")
	List<Subscription> findPermittedSubscriptionsOf(@Param("account") Account account);
	
	@Query("SELECT subs FROM Subscription subs "
			+ "WHERE subs.subscriber = :account "
			+ "AND (subs.state = '" + Subscription.State.ACTIVATED +"' "
			+ "OR subs.state = '" + Subscription.State.REQUESTED +"' "
			+ "OR subs.state = '" + Subscription.State.PERMITTED +"') ")
	List<Subscription> findImportantSubscriptionsOf(@Param("account") Account account);
	
	@Query("SELECT subs FROM Subscription subs "
			+ "WHERE subs.state = '" + Subscription.State.REQUESTED +"' ")
	Page<Subscription> findRequestedSubscriptionByPage(Pageable page);
}
