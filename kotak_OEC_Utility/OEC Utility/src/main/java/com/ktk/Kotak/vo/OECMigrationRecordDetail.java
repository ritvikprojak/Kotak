package com.ktk.Kotak.vo;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

public class OECMigrationRecordDetail {

	//private int id;
	private Date oecDate;
	private String crn;
	private String name;
	private String accountNumber;
	private String fileBarCode;
	private String doc_type;
	private String docId;
	private String migrationStatus;
	private String completedTime;
	private String status;
	private String url;
	
	public OECMigrationRecordDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	

	/*public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}*/


	

	

	public Date getOecDate() {
		return oecDate;
	}





	public void setOecDate(Date oecDate) {
		this.oecDate = oecDate;
	}





	public String getCrn() {
		return crn;
	}

	public void setCrn(String crn) {
		this.crn = crn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getFileBarCode() {
		return fileBarCode;
	}

	public void setFileBarCode(String fileBarCode) {
		this.fileBarCode = fileBarCode;
	}

	public String getDoc_type() {
		return doc_type;
	}

	public void setDoc_type(String doc_type) {
		this.doc_type = doc_type;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getMigrationStatus() {
		return migrationStatus;
	}

	public void setMigrationStatus(String migrationStatus) {
		this.migrationStatus = migrationStatus;
	}

	public String getCompletedTime() {
		return completedTime;
	}

	public void setCompletedTime(String completedTime) {
		this.completedTime = completedTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
