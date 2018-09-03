package com.k2l1.CreatedDocsServer.model.jpa.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="subscription")
public class Subscription implements Serializable{

	private static final long serialVersionUID = 8280412652680410273L;
	
	public static class State {
		public static final String REQUESTED = "REQUESTED";
		public static final String CANCELED = "CANCELED";
		public static final String PERMITTED = "PERMITTED";
		public static final String ACTIVATED = "ACTIVATED";
		public static final String EXPIRED = "EXPIRED";
	}

	@Id
	@Column(name="id")
	private String id;
	
	@ManyToOne
	@JoinColumn(name="subscriber_id")
	private Account subscriber;
	
	@Column(name="state")
	private String state;
	
	@Column(name="period_amount")
	private Long periodAmount;
	
	@Column(name="period_unit")
	private String periodUnit;
	
	@Column(name="charge_amount")
	private Long chargeAmount;
	
	@Column(name="requested_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date requestedAt;
	
	@Column(name="canceled_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date canceledAt;
	
	@Column(name="permitted_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date permittedAt;
	
	@Column(name="activated_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date activatedAt;
	
	@Column(name="expire_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expireAt;

	public Subscription() {}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Account getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Account subscriber) {
		this.subscriber = subscriber;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Long getPeriodAmount() {
		return periodAmount;
	}

	public void setPeriodAmount(Long periodAmount) {
		this.periodAmount = periodAmount;
	}

	public String getPeriodUnit() {
		return periodUnit;
	}

	public void setPeriodUnit(String periodUnit) {
		this.periodUnit = periodUnit;
	}

	public Long getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(Long chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public Date getRequestedAt() {
		return requestedAt;
	}

	public void setRequestedAt(Date requestedAt) {
		this.requestedAt = requestedAt;
	}

	public Date getCanceledAt() {
		return canceledAt;
	}

	public void setCanceledAt(Date canceledAt) {
		this.canceledAt = canceledAt;
	}

	public Date getPermittedAt() {
		return permittedAt;
	}

	public void setPermittedAt(Date permittedAt) {
		this.permittedAt = permittedAt;
	}

	public Date getActivatedAt() {
		return activatedAt;
	}

	public void setActivatedAt(Date activatedAt) {
		this.activatedAt = activatedAt;
	}

	public Date getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(Date expireAt) {
		this.expireAt = expireAt;
	}

	public String toString() {
		String ret = "{ " +
				"id: " + id + ", " +
				"subscriber: " + subscriber.toString() + ", " +
				"state: " + state + ", " +
				"periodAmount: " + periodAmount.toString() + ", " +
				"periodUnit: " + periodUnit + ", " +
				"chargeAmount: " + chargeAmount + "," +
				"requestedAt: " + requestedAt.toString() + ", " +
				"canceledAt: " + canceledAt + ", " +
				"permittedAt: " + permittedAt + ", " +
				"activatedAt: " + activatedAt + ", " +
				"expireAt: " + expireAt + 
				"}";
		return ret;
	}

}
