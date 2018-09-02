package com.k2l1.CreatedDocsServer.model.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name="account_settings")
public class AccountSetting implements Serializable{

	private static final long serialVersionUID = 5847444087695974003L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="introduction")
	private String introduction;
	

	@JsonBackReference
	@OneToOne
	@JoinColumn(name="account_id")
	private Account account;
	
	public AccountSetting() {}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}


	
	public String toString() {
		return "{"+
					"id:" + id
					+","+" introduction:" + introduction
				+"}";
	}
}
