package com.ktk.Kotak.filenetops;

import java.util.Date;

public class DatabaseDto 
{
	private String CRN;
	private String CustomerName ;
	private String AccountNumber;
	private String BarCode;
	private Date OECDate;
	private String DocType;
	private String filePath;

	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getCRN() {
		return CRN;
	}
	public void setCRN(String cRN) {
		CRN = cRN;
	}
	public String getCustomerName() {
		return CustomerName;
	}
	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}
	public String getAccountNumber() {
		return AccountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		AccountNumber = accountNumber;
	}
	public String getBarCode() {
		return BarCode;
	}
	public void setBarCode(String barCode) {
		BarCode = barCode;
	}
	public Date getOECDate() {
		return OECDate;
	}
	public void setOECDate(Date oECDate) {
		OECDate = oECDate;
	}
	public String getDocType() {
		return DocType;
	}
	public void setDocType(String docType) {
		DocType = docType;
	}

	
}
