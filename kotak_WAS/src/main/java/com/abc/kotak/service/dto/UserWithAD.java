package com.abc.kotak.service.dto;

public class UserWithAD {

	private String CreateAllowed; 
	private String ROLE_NAME; 
	private String LAST_NAME; 
	private String FIRST_NAME; 
	private String HRDocsAllowed; 
	private String DOCUMENT_TYPE;
	
		
	public UserWithAD() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public UserWithAD(String createAllowed, String rOLE_NAME, String lAST_NAME,
			String fIRST_NAME, String hRDocsAllowed, String dOCUMENT_TYPE) {
		super();
		CreateAllowed = createAllowed;
		ROLE_NAME = rOLE_NAME;
		LAST_NAME = lAST_NAME;
		FIRST_NAME = fIRST_NAME;
		HRDocsAllowed = hRDocsAllowed;
		DOCUMENT_TYPE = dOCUMENT_TYPE;
	}

	public String getCreateAllowed() {
		return CreateAllowed;
	}
	public void setCreateAllowed(String createAllowed) {
		CreateAllowed = createAllowed;
	}
	public String getROLE_NAME() {
		return ROLE_NAME;
	}
	public void setROLE_NAME(String rOLE_NAME) {
		ROLE_NAME = rOLE_NAME;
	}
	public String getLAST_NAME() {
		return LAST_NAME;
	}
	public void setLAST_NAME(String lAST_NAME) {
		LAST_NAME = lAST_NAME;
	}
	public String getFIRST_NAME() {
		return FIRST_NAME;
	}
	public void setFIRST_NAME(String fIRST_NAME) {
		FIRST_NAME = fIRST_NAME;
	}
	public String getHRDocsAllowed() {
		return HRDocsAllowed;
	}
	public void setHRDocsAllowed(String hRDocsAllowed) {
		HRDocsAllowed = hRDocsAllowed;
	}
	public String getDOCUMENT_TYPE() {
		return DOCUMENT_TYPE;
	}
	public void setDOCUMENT_TYPE(String dOCUMENT_TYPE) {
		DOCUMENT_TYPE = dOCUMENT_TYPE;
	}
	
	@Override
	public String toString() {
		return "UserWithAD [CreateAllowed=" + CreateAllowed + ", ROLE_NAME="
				+ ROLE_NAME + ", LAST_NAME=" + LAST_NAME + ", FIRST_NAME="
				+ FIRST_NAME + ", HRDocsAllowed=" + HRDocsAllowed
				+ ", DOCUMENT_TYPE=" + DOCUMENT_TYPE + "]";
	}
	
	
	
	
}
