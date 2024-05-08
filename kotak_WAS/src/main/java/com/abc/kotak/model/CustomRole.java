package com.abc.kotak.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name="CustRoles_Usr_Mapping",schema = "HRUPM")
public class CustomRole implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String uID;
	private String roleName;
	private String responsiblityName;
	private String domainName;
	private Character inclnORExcln;
	private String lob;
	private String loc;
	private String cc;
	private Boolean isActive;
	private Date lastWorkingDate;
//	private String modifiedBy;
//	private String approvedBy;
//	private Date modifiedOn;
//	private Date approvedOn;
	
	public CustomRole() {
		super();
	}
	
//	@Column(name="MODIFIED_BY")
//	public String getModifiedBy() {
//		return modifiedBy;
//	}
//
//	public void setModifiedBy(String modifiedBy) {
//		this.modifiedBy = modifiedBy;
//	}
//
//	@Column(name="APPROVED_BY")
//	public String getApprovedBy() {
//		return approvedBy;
//	}
//
//	public void setApprovedBy(String approvedBy) {
//		this.approvedBy = approvedBy;
//	}
//
//	@Column(name="MODIFIED_ON")
//	public Date getModifiedOn() {
//		return modifiedOn;
//	}
//
//	public void setModifiedOn(Date modifiedOn) {
//		this.modifiedOn = modifiedOn;
//	}
//
//	@Column(name="APPROVED_ON")
//	public Date getApprovedOn() {
//		return approvedOn;
//	}
//
//	public void setApprovedOn(Date approvedOn) {
//		this.approvedOn = approvedOn;
//	}
	
	@Column(name="LAST_WORKING_DATE")
	public Date getLastWorkingDate() {
		return lastWorkingDate;
	}

	public void setLastWorkingDate(Date lastWorkingDate) {
		this.lastWorkingDate = lastWorkingDate;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "staging")
	@SequenceGenerator(allocationSize = 1,name = "staging",sequenceName = "staging")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="PERSON_NUMBER")
	public String getuID() {
		return uID;
	}
	public void setuID(String uID) {
		this.uID = uID;
	}
		
	@Column(name="RESPONSIBILITY_NAME")
	public String getResponsiblityName() {
		return responsiblityName;
	}

	public void setResponsiblityName(String responsiblityName) {
		this.responsiblityName = responsiblityName;
	}

	@Column(name="Role_Name")
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Column(name="Domain_ID")
	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	@Column(name="Inclusion_Ecxlusion")
	public Character getInclnORExcln() {
		return inclnORExcln;
	}

	public void setInclnORExcln(Character inclnORExcln) {
		this.inclnORExcln = inclnORExcln;
	}

	@Column(name="LOB_CODE")
	public String getLob() {
		return lob;
	}

	public void setLob(String lob) {
		this.lob = lob;
	}

	@Column(name="LOC_CODE")
	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	@Column(name="CC_CODE")
	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}
	
	
	@Column(name="IS_ACTIVE")
	public Boolean isActive() {
		return isActive;
	}

	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	@Override
	public String toString() {
		return "CustomRole [id=" + id + ", uID=" + uID + ", roleName=" + roleName + ", responsiblityName="
				+ responsiblityName + ", domainName=" + domainName + ", inclnORExcln=" + inclnORExcln + ", lob=" + lob
				+ ", loc=" + loc + ", cc=" + cc + ", isActive=" + isActive + ", lastWorkingDate=" + lastWorkingDate+ "]";
	}

}
