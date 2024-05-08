package com.abc.kotak.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name="HRMS_HRRM",schema = "HRUPM")
public class Staging implements Serializable {
	
	@Id
	private String EMPLOYEE_NUMBER;
	private String ORACLE_EMPLOYEE_NUMBER; // Type changed from long to String
	private String COMPANY ;
	private String PERSON_ID;
	private String ATTRIBUTE3; 
	private String PREFIX;
	private String FIRST_NAME ;
	private String MIDDLE_NAMES;
	private String LAST_NAME;
	private String EMAIL_ADDRESS;
	private String GENDER;
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date DOB;
	private String MARITAL_STATUS;
	private String PADDRESS1;
	private String PADDRESS2;
	private String PADDRESS3;
	private String PCITY;
	private String PPIN ;
	private String TADDRESS1;
	private String TADDRESS2;
	private String TADDRESS3;
	private String TCITY;
	private String TPIN ;
	private String SUPERVISOR_EMP_NO; 
	private String SUP_COMPANY;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date DOJ;
	private String FATHER_HUSBAND_NAME; 
	private String DIVISION ;
	private String FUNCTION ;
	private Long FUNCTION_ID ;
	private Long DESIGNATION_ID;
	private String PAYROLL_ID;
	private String DESIGNATION_LABEL_ID;
	private String ROLE;
	private String LOC_CODE;
	private String LOCATION_NAME;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date DATE_EMPLOYEE_DATA_VERIFIED;
	private String LOB_CODE;
	private String LOB;
	private String CC_CODE;
	private String CC_NAME;
	private String CATEGORY;
	private String DOMAIN_LOGIN_ID;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date LAST_WORKING_DATE;
	private String RM_NAME;
	private String SUPERVISOR_NO; 
	private String SOURCE ;
	private String KPO_CODE ;
	private String SUPERVISOR_NAME; 
	private Long	 MOBILE_NUMBER ;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date ASSIGNMENT_CHANGE_DATE;
	private String LOC_CODE_NEW;
	private String ING_EMP_TYPE;
	private String SEGMENT;
	private String ROLE_ID;
	private String PAN_NO ;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date DOJ_KOTAK_GROUP ;
	private String RM_EMAIL;
	private String GRADE_ID;
	private String HR_PERSON_NUMBER;
	/*private String EXTRA_1;
	private String EXTRA_2;
	private String EXTRA_3;
	private String EXTRA_4;
	private String EXTRA_5;*/
	
	
	public Staging() {
	}


	public String getEMPLOYEE_NUMBER() {
		return EMPLOYEE_NUMBER;
	}


	public void setEMPLOYEE_NUMBER(String eMPLOYEE_NUMBER) {
		EMPLOYEE_NUMBER = eMPLOYEE_NUMBER;
	}


	public String getORACLE_EMPLOYEE_NUMBER() {
		return ORACLE_EMPLOYEE_NUMBER;
	}


	public void setORACLE_EMPLOYEE_NUMBER(String oRACLE_EMPLOYEE_NUMBER) {
		ORACLE_EMPLOYEE_NUMBER = oRACLE_EMPLOYEE_NUMBER;
	}


	public String getCOMPANY() {
		return COMPANY;
	}


	public void setCOMPANY(String cOMPANY) {
		COMPANY = cOMPANY;
	}


	public String getPERSON_ID() {
		return PERSON_ID;
	}


	public void setPERSON_ID(String pERSON_ID) {
		PERSON_ID = pERSON_ID;
	}


	public String getATTRIBUTE3() {
		return ATTRIBUTE3;
	}


	public void setATTRIBUTE3(String aTTRIBUTE3) {
		ATTRIBUTE3 = aTTRIBUTE3;
	}


	public String getPREFIX() {
		return PREFIX;
	}


	public void setPREFIX(String pREFIX) {
		PREFIX = pREFIX;
	}


	public String getFIRST_NAME() {
		return FIRST_NAME;
	}


	public void setFIRST_NAME(String fIRST_NAME) {
		FIRST_NAME = fIRST_NAME;
	}


	public String getMIDDLE_NAMES() {
		return MIDDLE_NAMES;
	}


	public void setMIDDLE_NAMES(String mIDDLE_NAMES) {
		MIDDLE_NAMES = mIDDLE_NAMES;
	}


	public String getLAST_NAME() {
		return LAST_NAME;
	}


	public void setLAST_NAME(String lAST_NAME) {
		LAST_NAME = lAST_NAME;
	}


	public String getEMAIL_ADDRESS() {
		return EMAIL_ADDRESS;
	}


	public void setEMAIL_ADDRESS(String eMAIL_ADDRESS) {
		EMAIL_ADDRESS = eMAIL_ADDRESS;
	}


	public String getGENDER() {
		return GENDER;
	}


	public void setGENDER(String gENDER) {
		GENDER = gENDER;
	}


	public Date getDOB() {
		return DOB;
	}


	public void setDOB(Date dOB) {
		DOB = dOB;
	}


	public String getMARITAL_STATUS() {
		return MARITAL_STATUS;
	}


	public void setMARITAL_STATUS(String mARITAL_STATUS) {
		MARITAL_STATUS = mARITAL_STATUS;
	}


	public String getPADDRESS1() {
		return PADDRESS1;
	}


	public void setPADDRESS1(String pADDRESS1) {
		PADDRESS1 = pADDRESS1;
	}


	public String getPADDRESS2() {
		return PADDRESS2;
	}


	public void setPADDRESS2(String pADDRESS2) {
		PADDRESS2 = pADDRESS2;
	}


	public String getPADDRESS3() {
		return PADDRESS3;
	}


	public void setPADDRESS3(String pADDRESS3) {
		PADDRESS3 = pADDRESS3;
	}


	public String getPCITY() {
		return PCITY;
	}


	public void setPCITY(String pCITY) {
		PCITY = pCITY;
	}


	public String getPPIN() {
		return PPIN;
	}


	public void setPPIN(String pPIN) {
		PPIN = pPIN;
	}


	public String getTADDRESS1() {
		return TADDRESS1;
	}


	public void setTADDRESS1(String tADDRESS1) {
		TADDRESS1 = tADDRESS1;
	}


	public String getTADDRESS2() {
		return TADDRESS2;
	}


	public void setTADDRESS2(String tADDRESS2) {
		TADDRESS2 = tADDRESS2;
	}


	public String getTADDRESS3() {
		return TADDRESS3;
	}


	public void setTADDRESS3(String tADDRESS3) {
		TADDRESS3 = tADDRESS3;
	}


	public String getTCITY() {
		return TCITY;
	}


	public void setTCITY(String tCITY) {
		TCITY = tCITY;
	}


	public String getTPIN() {
		return TPIN;
	}


	public void setTPIN(String tPIN) {
		TPIN = tPIN;
	}


	public String getSUPERVISOR_EMP_NO() {
		return SUPERVISOR_EMP_NO;
	}


	public void setSUPERVISOR_EMP_NO(String sUPERVISOR_EMP_NO) {
		SUPERVISOR_EMP_NO = sUPERVISOR_EMP_NO;
	}


	public String getSUP_COMPANY() {
		return SUP_COMPANY;
	}


	public void setSUP_COMPANY(String sUP_COMPANY) {
		SUP_COMPANY = sUP_COMPANY;
	}


	public Date getDOJ() {
		return DOJ;
	}


	public void setDOJ(Date dOJ) {
		DOJ = dOJ;
	}


	public String getFATHER_HUSBAND_NAME() {
		return FATHER_HUSBAND_NAME;
	}


	public void setFATHER_HUSBAND_NAME(String fATHER_HUSBAND_NAME) {
		FATHER_HUSBAND_NAME = fATHER_HUSBAND_NAME;
	}


	public String getDIVISION() {
		return DIVISION;
	}


	public void setDIVISION(String dIVISION) {
		DIVISION = dIVISION;
	}


	public String getFUNCTION() {
		return FUNCTION;
	}


	public void setFUNCTION(String fUNCTION) {
		FUNCTION = fUNCTION;
	}


	public Long getFUNCTION_ID() {
		return FUNCTION_ID;
	}


	public void setFUNCTION_ID(Long fUNCTION_ID) {
		FUNCTION_ID = fUNCTION_ID;
	}


	public Long getDESIGNATION_ID() {
		return DESIGNATION_ID;
	}


	public void setDESIGNATION_ID(Long dESIGNATION_ID) {
		DESIGNATION_ID = dESIGNATION_ID;
	}


	public String getPAYROLL_ID() {
		return PAYROLL_ID;
	}


	public void setPAYROLL_ID(String pAYROLL_ID) {
		PAYROLL_ID = pAYROLL_ID;
	}


	public String getDESIGNATION_LABEL_ID() {
		return DESIGNATION_LABEL_ID;
	}


	public void setDESIGNATION_LABEL_ID(String dESIGNATION_LABEL_ID) {
		DESIGNATION_LABEL_ID = dESIGNATION_LABEL_ID;
	}


	public String getROLE() {
		return ROLE;
	}


	public void setROLE(String rOLE) {
		ROLE = rOLE;
	}


	public String getLOC_CODE() {
		return LOC_CODE;
	}


	public void setLOC_CODE(String lOC_CODE) {
		LOC_CODE = lOC_CODE;
	}


	public String getLOCATION_NAME() {
		return LOCATION_NAME;
	}


	public void setLOCATION_NAME(String lOCATION_NAME) {
		LOCATION_NAME = lOCATION_NAME;
	}


	public Date getDATE_EMPLOYEE_DATA_VERIFIED() {
		return DATE_EMPLOYEE_DATA_VERIFIED;
	}


	public void setDATE_EMPLOYEE_DATA_VERIFIED(Date dATE_EMPLOYEE_DATA_VERIFIED) {
		DATE_EMPLOYEE_DATA_VERIFIED = dATE_EMPLOYEE_DATA_VERIFIED;
	}


	public String getLOB_CODE() {
		return LOB_CODE;
	}


	public void setLOB_CODE(String lOB_CODE) {
		LOB_CODE = lOB_CODE;
	}


	public String getLOB() {
		return LOB;
	}


	public void setLOB(String lOB) {
		LOB = lOB;
	}


	public String getCC_CODE() {
		return CC_CODE;
	}


	public void setCC_CODE(String cC_CODE) {
		CC_CODE = cC_CODE;
	}


	public String getCC_NAME() {
		return CC_NAME;
	}


	public void setCC_NAME(String cC_NAME) {
		CC_NAME = cC_NAME;
	}


	public String getCATEGORY() {
		return CATEGORY;
	}


	public void setCATEGORY(String cATEGORY) {
		CATEGORY = cATEGORY;
	}


	public String getDOMAIN_LOGIN_ID() {
		return DOMAIN_LOGIN_ID;
	}


	public void setDOMAIN_LOGIN_ID(String dOMAIN_LOGIN_ID) {
		DOMAIN_LOGIN_ID = dOMAIN_LOGIN_ID;
	}


	public Date getLAST_WORKING_DATE() {
		return LAST_WORKING_DATE;
	}


	public void setLAST_WORKING_DATE(Date lAST_WORKING_DATE) {
		LAST_WORKING_DATE = lAST_WORKING_DATE;
	}


	public String getRM_NAME() {
		return RM_NAME;
	}


	public void setRM_NAME(String rM_NAME) {
		RM_NAME = rM_NAME;
	}


	public String getSUPERVISOR_NO() {
		return SUPERVISOR_NO;
	}


	public void setSUPERVISOR_NO(String sUPERVISOR_NO) {
		SUPERVISOR_NO = sUPERVISOR_NO;
	}


	public String getSOURCE() {
		return SOURCE;
	}


	public void setSOURCE(String sOURCE) {
		SOURCE = sOURCE;
	}


	public String getKPO_CODE() {
		return KPO_CODE;
	}


	public void setKPO_CODE(String kPO_CODE) {
		KPO_CODE = kPO_CODE;
	}


	public String getSUPERVISOR_NAME() {
		return SUPERVISOR_NAME;
	}


	public void setSUPERVISOR_NAME(String sUPERVISOR_NAME) {
		SUPERVISOR_NAME = sUPERVISOR_NAME;
	}


	public Long getMOBILE_NUMBER() {
		return MOBILE_NUMBER;
	}


	public void setMOBILE_NUMBER(Long mOBILE_NUMBER) {
		MOBILE_NUMBER = mOBILE_NUMBER;
	}


	public Date getASSIGNMENT_CHANGE_DATE() {
		return ASSIGNMENT_CHANGE_DATE;
	}


	public void setASSIGNMENT_CHANGE_DATE(Date aSSIGNMENT_CHANGE_DATE) {
		ASSIGNMENT_CHANGE_DATE = aSSIGNMENT_CHANGE_DATE;
	}


	public String getLOC_CODE_NEW() {
		return LOC_CODE_NEW;
	}


	public void setLOC_CODE_NEW(String lOC_CODE_NEW) {
		LOC_CODE_NEW = lOC_CODE_NEW;
	}


	public String getING_EMP_TYPE() {
		return ING_EMP_TYPE;
	}


	public void setING_EMP_TYPE(String iNG_EMP_TYPE) {
		ING_EMP_TYPE = iNG_EMP_TYPE;
	}


	public String getSEGMENT() {
		return SEGMENT;
	}


	public void setSEGMENT(String sEGMENT) {
		SEGMENT = sEGMENT;
	}


	public String getROLE_ID() {
		return ROLE_ID;
	}


	public void setROLE_ID(String rOLE_ID) {
		ROLE_ID = rOLE_ID;
	}


	public String getPAN_NO() {
		return PAN_NO;
	}


	public void setPAN_NO(String pAN_NO) {
		PAN_NO = pAN_NO;
	}


	public Date getDOJ_KOTAK_GROUP() {
		return DOJ_KOTAK_GROUP;
	}


	public void setDOJ_KOTAK_GROUP(Date dOJ_KOTAK_GROUP) {
		DOJ_KOTAK_GROUP = dOJ_KOTAK_GROUP;
	}


	public String getRM_EMAIL() {
		return RM_EMAIL;
	}


	public void setRM_EMAIL(String rM_EMAIL) {
		RM_EMAIL = rM_EMAIL;
	}


	public String getGRADE_ID() {
		return GRADE_ID;
	}


	public void setGRADE_ID(String gRADE_ID) {
		GRADE_ID = gRADE_ID;
	}


	public String getHR_PERSON_NUMBER() {
		return HR_PERSON_NUMBER;
	}


	public void setHR_PERSON_NUMBER(String hR_PERSON_NUMBER) {
		HR_PERSON_NUMBER = hR_PERSON_NUMBER;
	}


	@Override
	public String toString() {
		return "Staging [FIRST_NAME=" + FIRST_NAME + ", LAST_NAME=" + LAST_NAME
				+ "]";
	}
	
	

}
