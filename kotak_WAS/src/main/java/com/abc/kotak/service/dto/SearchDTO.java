package com.abc.kotak.service.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchDTO {
	
	private String EmployeeCode;
	private String LOB;
	private String LOC;
	private String CC;
	private String status;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date DateOfJoining;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date DateOfGroupJoining;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date LastDate;
	
	public SearchDTO() {
		super();
	}

	public String getEmployeeCode() {
		return EmployeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.EmployeeCode = employeeCode;
	}

	public String getLOB() {
		return LOB;
	}

	public void setLOB(String lob) {
		this.LOB = lob;
	}

	public String getLOC() {
		return LOC;
	}

	public void setLOC(String loc) {
		this.LOC = loc;
	}

	public String getCC() {
		return CC;
	}

	public void setCC(String cc) {
		this.CC = cc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDateOfJoining() {
		return DateOfJoining;
	}

	public void setDateOfJoining(Date dateOfJoining) {
		this.DateOfJoining = dateOfJoining;
	}

	public Date getDateOfGroupJoining() {
		return DateOfGroupJoining;
	}

	public void setDateOfGroupJoining(Date dateOfGroupJoining) {
		this.DateOfGroupJoining = dateOfGroupJoining;
	}

	public Date getLastDate() {
		return LastDate;
	}

	public void setLastDate(Date lastDate) {
		LastDate = lastDate;
	}
	
	@Override
	public String toString() {
		return "SearchDTO [EmployeeCode=" + EmployeeCode + ", LOB=" + LOB
				+ ", LOC=" + LOC + ", CC=" + CC + ", status=" + status
				+ ", DateOfJoining=" + DateOfJoining + ", DateOfGroupJoining="
				+ DateOfGroupJoining + ", LastDate=" + LastDate + "]";
	}


}
