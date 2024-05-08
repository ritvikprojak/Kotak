package com.abc.kotak.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.abc.kotak.model.Documents;
import com.abc.kotak.model.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

public class RoleDTO {
	private Long roleId;
	private String roleName;
	private Boolean read;
	private Boolean create;
	private Boolean copy;
	private Boolean modify;
	private Boolean print;
	private Boolean delete;
	private Boolean viewHR;
	private Boolean active;
	private Boolean approved;
	private String[] grades;
	private String createdBy;
	
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date createdOn;
	private String approvedBy;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date approvedOn;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date endDate;
	private Boolean ctc;
	
	private Boolean isCustomRole;
	private String incluORExcl;
	private List<String> relationship = new ArrayList<String>() ;
	
	private List<Documents> documents = new ArrayList<Documents>();

	public RoleDTO() {
		super();
	}

		
	public RoleDTO(Role role) {
		super();
		this.roleId = role.getRoleId();
		this.roleName = role.getRoleName();
		this.read = role.getRead();
		this.create = role.getCreate();
		this.copy = role.getCopy();
		this.modify = role.getModify();
		this.print = role.getPrint();
		this.delete = role.getDelete();
		this.viewHR = role.getViewHR();
		this.active = role.getActive();
		this.approved = role.getApproved();
		this.grades = role.getGrades();
		this.createdBy = role.getCreatedBy();
		this.createdOn = role.getCreatedOn();
		this.approvedBy = role.getApprovedBy();
		this.approvedOn = role.getApprovedOn();
		this.endDate = role.getEndDate();
		this.ctc = role.getCTC();
		this.isCustomRole = role.getIsCustomRole();
		this.documents = role.getDocuments();
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public Boolean getCreate() {
		return create;
	}

	public void setCreate(Boolean create) {
		this.create = create;
	}

	public Boolean getCopy() {
		return copy;
	}

	public void setCopy(Boolean copy) {
		this.copy = copy;
	}

	public Boolean getModify() {
		return modify;
	}

	public void setModify(Boolean modify) {
		this.modify = modify;
	}

	public Boolean getPrint() {
		return print;
	}

	public void setPrint(Boolean print) {
		this.print = print;
	}

	public Boolean getDelete() {
		return delete;
	}

	public void setDelete(Boolean delete) {
		this.delete = delete;
	}

	public Boolean getViewHR() {
		return viewHR;
	}

	public void setViewHR(Boolean viewHR) {
		this.viewHR = viewHR;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public String[] getGrades() {
		return grades;
	}

	public void setGrades(String[] grades) {
		this.grades = grades;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Boolean getIsCustomRole() {
		return isCustomRole;
	}

	public void setIsCustomRole(Boolean isCustomRole) {
		this.isCustomRole = isCustomRole;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
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

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Boolean getCtc() {
		return ctc;
	}

	public void setCtc(Boolean ctc) {
		this.ctc = ctc;
	}

	public List<Documents> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Documents> documents) {
		this.documents = documents;
	}

	public List<String> getRelationship() {
		return relationship;
	}

	public void setRelationship(List<String> relationship) {
		this.relationship = relationship;
	}
	
	public String getIncluORExcl() {
		return incluORExcl;
	}

	public void setIncluORExcl(String incluORExcl) {
		this.incluORExcl = incluORExcl;
	}

	@Override
	public String toString() {
		return "RoleDTO [roleId=" + roleId + ", roleName=" + roleName
				+ ", read=" + read + ", create=" + create + ", copy=" + copy
				+ ", modify=" + modify + ", print=" + print + ", delete="
				+ delete + ", viewHR=" + viewHR + ", active=" + active
				+ ", approved=" + approved + ", grades="
				+ Arrays.toString(grades) + ", createdBy=" + createdBy
				+ ", isCustomRole=" + isCustomRole + ", createdOn=" + createdOn
				+ ", approvedBy=" + approvedBy + ", approvedOn=" + approvedOn
				+ ", endDate=" + endDate + ", ctc=" + ctc + ", documents="
				+ documents + "]";
	}
	
	
	
}
