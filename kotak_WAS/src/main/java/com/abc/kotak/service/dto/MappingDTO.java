package com.abc.kotak.service.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MappingDTO {
	

	private String userId;
	private String employeeNumber;
	private String role;
	
	@JsonProperty
	private boolean isCustomRole;
	private String modifiedBy;
	private Date modifiedOn;
	private String approvedBy;
	private Date approvedOn;
	
	
	public MappingDTO() {
		super();
	}
	

	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getEmployeeNumber() {
		return employeeNumber;
	}


	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public boolean isCustomRole() {
		return isCustomRole;
	}


	public void setCustomRole(boolean isCustomRole) {
		this.isCustomRole = isCustomRole;
	}


	public String getModifiedBy() {
		return modifiedBy;
	}


	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}


	public Date getModifiedOn() {
		return modifiedOn;
	}


	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}


	public String getApprovedBy() {
		return approvedBy;
	}


	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}


	public Date getApprovedOn() {
		return approvedOn;
	}


	public void setApprovedOn(Date approvedOn) {
		this.approvedOn = approvedOn;
	}


	@Override
	public String toString() {
		return "MappingDTO [userId=" + userId + ", employeeNumber=" + employeeNumber + ", role=" + role
				+ ", isCustomRole=" + isCustomRole + ", modifiedBy=" + modifiedBy + ", modifiedOn=" + modifiedOn
				+ ", approvedBy=" + approvedBy + ", approvedOn=" + approvedOn + "]";
	}


	
	
}
