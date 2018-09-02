package com.k2l1.CreatedDocsServer.model.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="accounts")
public class Account implements Serializable{

	private static final long serialVersionUID = -4163953969858437768L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="username")
	private String username;
	
	@Column(name="pass_word")
	private String password;
	
	@Column(name="create_timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTimestamp;
	
	@Column(name="update_timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatesTimestamp;
	
	/*entity mappings*/
	
	@OneToOne(mappedBy="account", fetch = FetchType.LAZY)
	private AccountSetting accountSetting;

	@OneToMany(fetch = FetchType.LAZY, mappedBy="subscriber")
	private Set<Subscription> articles = new HashSet<Subscription>();

	public Account(String username, String plainPassword) {}
	public Account() {}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Date createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public Date getUpdatesTimestamp() {
		return updatesTimestamp;
	}

	public void setUpdatesTimestamp(Date updatesTimestamp) {
		this.updatesTimestamp = updatesTimestamp;
	}
	
	
	
	public AccountSetting getAccountSetting() {
		return accountSetting;
	}

	public void setAccountSetting(AccountSetting accountSetting) {
		this.accountSetting = accountSetting;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		
		if(this.id==null)
			id=-9999l;
		
		sb.append(String.format("%s : %s", "id", Long.toString(this.id)));
		sb.append(", ");
		sb.append(String.format("%s : %s", "username", this.username));
		sb.append(", ");
		sb.append(String.format("%s : %s", "password", this.password));
		sb.append(", ");
		if(this.createTimestamp != null && this.updatesTimestamp != null) {
			sb.append(String.format("%s : %s", "createTimestamp", this.createTimestamp.toString() ));
			sb.append(", ");
			sb.append(String.format("%s : %s", "updatesTimestamp", this.updatesTimestamp.toString() ));
		}
		
		if(accountSetting!=null) {
			sb.append(",");
			sb.append(String.format("%s : %s", "accountSetting", this.accountSetting));
		}else {
			sb.append(",");
			sb.append(String.format("%s : %s", "accountSetting", "null"));

		}
		
		sb.append("}");
		return sb.toString();
		
	}

}
