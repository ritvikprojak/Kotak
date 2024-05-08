package com.abc.kotak.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="USER_LOGIN_TABLE", schema = "HRUPM")
public class UserLogin implements Serializable{

	
	private String domainId;
	
	@Id
	@JsonFormat(pattern = "yyyy-mm-dd")
	private Date login_time;
	
	@JsonFormat(pattern = "yyyy-mm-dd")
	private Date logout_time;

	private String system;
	
	
	public UserLogin() {
	}

	@Column(name = "DOMAIN_ID")
	public String getDomainId() {
		return domainId;
	}

	
	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	@Column(name = "LOGIN_TIME")
	public Date getLogin_time() {
		return login_time;
	}


	public void setLogin_time(Date login_time) {
		this.login_time = login_time;
	}

	@Column(name = "LOGOUT_TIME")
	public Date getLogout_time() {
		return logout_time;
	}

	@Column(name="SYSTEM")
	public void setLogout_time(Date logout_time) {
		this.logout_time = logout_time;
	}
	
	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}
	
}
