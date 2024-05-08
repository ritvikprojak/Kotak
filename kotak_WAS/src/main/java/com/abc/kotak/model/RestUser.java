
package com.abc.kotak.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"USER_ID",
"SUP_ID",
"FIRST_NAME",
"LAST_NAME",
"DESIGNATION_CODE",
"DESIGNATION_DESC",
"SECURITY_CLASS",
"WORK_CLASS_BOT",
"AD_USER_ID",
"SIGNATURE_ID",
"OFFICER_STATUS",
"DA_LEVEL_BOT",
"DA_AMOUNT_BOT",
"MOBILE_PHONE",
"EMAIL",
"SOL_ID",
"DIV_CODE",
"BOT_STATUS",
"APPLICATION_CODE",
"APPLICATION_SECURITY_CLASS",
"WORK_CLASS_OAT",
"EXPIRY_DATE",
"DA_LEVEL_OAT",
"DA_AMOUNT_OAT",
"SIGN_ON_STATUS",
"ACTIVE_STATUS",
"FUNCTION_CODE1",
"FUNCTION_CODE2",
"FUNCTION_CODE3",
"OAT_STATUS"
})
public class RestUser {

@JsonProperty("USER_ID")
private String uSERID;
@JsonProperty("SUP_ID")
private String sUPID;
@JsonProperty("FIRST_NAME")
private String fIRSTNAME;
@JsonProperty("LAST_NAME")
private String lASTNAME;
@JsonProperty("DESIGNATION_CODE")
private String dESIGNATIONCODE;
@JsonProperty("DESIGNATION_DESC")
private String dESIGNATIONDESC;
@JsonProperty("SECURITY_CLASS")
private String sECURITYCLASS;
@JsonProperty("WORK_CLASS_BOT")
private String wORKCLASSBOT;
@JsonProperty("AD_USER_ID")
private String aDUSERID;
@JsonProperty("SIGNATURE_ID")
private Object sIGNATUREID;
@JsonProperty("OFFICER_STATUS")
private String oFFICERSTATUS;
@JsonProperty("DA_LEVEL_BOT")
private Object dALEVELBOT;
@JsonProperty("DA_AMOUNT_BOT")
private String dAAMOUNTBOT;
@JsonProperty("MOBILE_PHONE")
private Object mOBILEPHONE;
@JsonProperty("EMAIL")
private Object eMAIL;
@JsonProperty("SOL_ID")
private String sOLID;
@JsonProperty("DIV_CODE")
private String dIVCODE;
@JsonProperty("BOT_STATUS")
private String bOTSTATUS;
@JsonProperty("APPLICATION_CODE")
private String aPPLICATIONCODE;
@JsonProperty("APPLICATION_SECURITY_CLASS")
private String aPPLICATIONSECURITYCLASS;
@JsonProperty("WORK_CLASS_OAT")
private String wORKCLASSOAT;
@JsonProperty("EXPIRY_DATE")
private String eXPIRYDATE;
@JsonProperty("DA_LEVEL_OAT")
private String dALEVELOAT;
@JsonProperty("DA_AMOUNT_OAT")
private String dAAMOUNTOAT;
@JsonProperty("SIGN_ON_STATUS")
private String sIGNONSTATUS;
@JsonProperty("ACTIVE_STATUS")
private String aCTIVESTATUS;
@JsonProperty("FUNCTION_CODE1")
private String fUNCTIONCODE1;
@JsonProperty("FUNCTION_CODE2")
private String fUNCTIONCODE2;
@JsonProperty("FUNCTION_CODE3")
private Object fUNCTIONCODE3;
@JsonProperty("OAT_STATUS")
private String oATSTATUS;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("USER_ID")
public String getUSERID() {
return uSERID;
}

@JsonProperty("USER_ID")
public void setUSERID(String uSERID) {
this.uSERID = uSERID;
}

@JsonProperty("SUP_ID")
public String getSUPID() {
return sUPID;
}

@JsonProperty("SUP_ID")
public void setSUPID(String sUPID) {
this.sUPID = sUPID;
}

@JsonProperty("FIRST_NAME")
public String getFIRSTNAME() {
return fIRSTNAME;
}

@JsonProperty("FIRST_NAME")
public void setFIRSTNAME(String fIRSTNAME) {
this.fIRSTNAME = fIRSTNAME;
}

@JsonProperty("LAST_NAME")
public String getLASTNAME() {
return lASTNAME;
}

@JsonProperty("LAST_NAME")
public void setLASTNAME(String lASTNAME) {
this.lASTNAME = lASTNAME;
}

@JsonProperty("DESIGNATION_CODE")
public String getDESIGNATIONCODE() {
return dESIGNATIONCODE;
}

@JsonProperty("DESIGNATION_CODE")
public void setDESIGNATIONCODE(String dESIGNATIONCODE) {
this.dESIGNATIONCODE = dESIGNATIONCODE;
}

@JsonProperty("DESIGNATION_DESC")
public String getDESIGNATIONDESC() {
return dESIGNATIONDESC;
}

@JsonProperty("DESIGNATION_DESC")
public void setDESIGNATIONDESC(String dESIGNATIONDESC) {
this.dESIGNATIONDESC = dESIGNATIONDESC;
}

@JsonProperty("SECURITY_CLASS")
public String getSECURITYCLASS() {
return sECURITYCLASS;
}

@JsonProperty("SECURITY_CLASS")
public void setSECURITYCLASS(String sECURITYCLASS) {
this.sECURITYCLASS = sECURITYCLASS;
}

@JsonProperty("WORK_CLASS_BOT")
public String getWORKCLASSBOT() {
return wORKCLASSBOT;
}

@JsonProperty("WORK_CLASS_BOT")
public void setWORKCLASSBOT(String wORKCLASSBOT) {
this.wORKCLASSBOT = wORKCLASSBOT;
}

@JsonProperty("AD_USER_ID")
public String getADUSERID() {
return aDUSERID;
}

@JsonProperty("AD_USER_ID")
public void setADUSERID(String aDUSERID) {
this.aDUSERID = aDUSERID;
}

@JsonProperty("SIGNATURE_ID")
public Object getSIGNATUREID() {
return sIGNATUREID;
}

@JsonProperty("SIGNATURE_ID")
public void setSIGNATUREID(Object sIGNATUREID) {
this.sIGNATUREID = sIGNATUREID;
}

@JsonProperty("OFFICER_STATUS")
public String getOFFICERSTATUS() {
return oFFICERSTATUS;
}

@JsonProperty("OFFICER_STATUS")
public void setOFFICERSTATUS(String oFFICERSTATUS) {
this.oFFICERSTATUS = oFFICERSTATUS;
}

@JsonProperty("DA_LEVEL_BOT")
public Object getDALEVELBOT() {
return dALEVELBOT;
}

@JsonProperty("DA_LEVEL_BOT")
public void setDALEVELBOT(Object dALEVELBOT) {
this.dALEVELBOT = dALEVELBOT;
}

@JsonProperty("DA_AMOUNT_BOT")
public String getDAAMOUNTBOT() {
return dAAMOUNTBOT;
}

@JsonProperty("DA_AMOUNT_BOT")
public void setDAAMOUNTBOT(String dAAMOUNTBOT) {
this.dAAMOUNTBOT = dAAMOUNTBOT;
}

@JsonProperty("MOBILE_PHONE")
public Object getMOBILEPHONE() {
return mOBILEPHONE;
}

@JsonProperty("MOBILE_PHONE")
public void setMOBILEPHONE(Object mOBILEPHONE) {
this.mOBILEPHONE = mOBILEPHONE;
}

@JsonProperty("EMAIL")
public Object getEMAIL() {
return eMAIL;
}

@JsonProperty("EMAIL")
public void setEMAIL(Object eMAIL) {
this.eMAIL = eMAIL;
}

@JsonProperty("SOL_ID")
public String getSOLID() {
return sOLID;
}

@JsonProperty("SOL_ID")
public void setSOLID(String sOLID) {
this.sOLID = sOLID;
}

@JsonProperty("DIV_CODE")
public String getDIVCODE() {
return dIVCODE;
}

@JsonProperty("DIV_CODE")
public void setDIVCODE(String dIVCODE) {
this.dIVCODE = dIVCODE;
}

@JsonProperty("BOT_STATUS")
public String getBOTSTATUS() {
return bOTSTATUS;
}

@JsonProperty("BOT_STATUS")
public void setBOTSTATUS(String bOTSTATUS) {
this.bOTSTATUS = bOTSTATUS;
}

@JsonProperty("APPLICATION_CODE")
public String getAPPLICATIONCODE() {
return aPPLICATIONCODE;
}

@JsonProperty("APPLICATION_CODE")
public void setAPPLICATIONCODE(String aPPLICATIONCODE) {
this.aPPLICATIONCODE = aPPLICATIONCODE;
}

@JsonProperty("APPLICATION_SECURITY_CLASS")
public String getAPPLICATIONSECURITYCLASS() {
return aPPLICATIONSECURITYCLASS;
}

@JsonProperty("APPLICATION_SECURITY_CLASS")
public void setAPPLICATIONSECURITYCLASS(String aPPLICATIONSECURITYCLASS) {
this.aPPLICATIONSECURITYCLASS = aPPLICATIONSECURITYCLASS;
}

@JsonProperty("WORK_CLASS_OAT")
public String getWORKCLASSOAT() {
return wORKCLASSOAT;
}

@JsonProperty("WORK_CLASS_OAT")
public void setWORKCLASSOAT(String wORKCLASSOAT) {
this.wORKCLASSOAT = wORKCLASSOAT;
}

@JsonProperty("EXPIRY_DATE")
public String getEXPIRYDATE() {
return eXPIRYDATE;
}

@JsonProperty("EXPIRY_DATE")
public void setEXPIRYDATE(String eXPIRYDATE) {
this.eXPIRYDATE = eXPIRYDATE;
}

@JsonProperty("DA_LEVEL_OAT")
public String getDALEVELOAT() {
return dALEVELOAT;
}

@JsonProperty("DA_LEVEL_OAT")
public void setDALEVELOAT(String dALEVELOAT) {
this.dALEVELOAT = dALEVELOAT;
}

@JsonProperty("DA_AMOUNT_OAT")
public String getDAAMOUNTOAT() {
return dAAMOUNTOAT;
}

@JsonProperty("DA_AMOUNT_OAT")
public void setDAAMOUNTOAT(String dAAMOUNTOAT) {
this.dAAMOUNTOAT = dAAMOUNTOAT;
}

@JsonProperty("SIGN_ON_STATUS")
public String getSIGNONSTATUS() {
return sIGNONSTATUS;
}

@JsonProperty("SIGN_ON_STATUS")
public void setSIGNONSTATUS(String sIGNONSTATUS) {
this.sIGNONSTATUS = sIGNONSTATUS;
}

@JsonProperty("ACTIVE_STATUS")
public String getACTIVESTATUS() {
return aCTIVESTATUS;
}

@JsonProperty("ACTIVE_STATUS")
public void setACTIVESTATUS(String aCTIVESTATUS) {
this.aCTIVESTATUS = aCTIVESTATUS;
}

@JsonProperty("FUNCTION_CODE1")
public String getFUNCTIONCODE1() {
return fUNCTIONCODE1;
}

@JsonProperty("FUNCTION_CODE1")
public void setFUNCTIONCODE1(String fUNCTIONCODE1) {
this.fUNCTIONCODE1 = fUNCTIONCODE1;
}

@JsonProperty("FUNCTION_CODE2")
public String getFUNCTIONCODE2() {
return fUNCTIONCODE2;
}

@JsonProperty("FUNCTION_CODE2")
public void setFUNCTIONCODE2(String fUNCTIONCODE2) {
this.fUNCTIONCODE2 = fUNCTIONCODE2;
}

@JsonProperty("FUNCTION_CODE3")
public Object getFUNCTIONCODE3() {
return fUNCTIONCODE3;
}

@JsonProperty("FUNCTION_CODE3")
public void setFUNCTIONCODE3(Object fUNCTIONCODE3) {
this.fUNCTIONCODE3 = fUNCTIONCODE3;
}

@JsonProperty("OAT_STATUS")
public String getOATSTATUS() {
return oATSTATUS;
}

@JsonProperty("OAT_STATUS")
public void setOATSTATUS(String oATSTATUS) {
this.oATSTATUS = oATSTATUS;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}