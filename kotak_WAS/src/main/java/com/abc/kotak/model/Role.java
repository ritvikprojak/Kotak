package com.abc.kotak.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * @author qcc
 *
 */
@Entity
@Table(name="Roles",schema = "HRUPM")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
property = "roleId")
public class Role implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
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
	private Boolean isCustomRole;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date createdOn;
	private String approvedBy;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date approvedOn;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date endDate;
	private Boolean ctc;
	

	private List<Documents> documents = new ArrayList<Documents>();
	
	public Role() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "staging")
	@SequenceGenerator(allocationSize = 1,name = "staging",sequenceName = "staging")
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	
	@ManyToMany
	@JoinTable(name = "Role_Document_Mapping",schema = "HRUPM",
	joinColumns = @JoinColumn(name = "Role_ID", unique=false))
	public List<Documents> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Documents> documents) {
		this.documents = documents;
	}
	
	@Column(name="is_CTC")
	public Boolean getCTC() {
		return ctc;
	}

	public void setCTC(Boolean ctc) {
		this.ctc = ctc;
	}

	@Column(name="rolename",unique = true)
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Column(name="read_access")
	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	@Column(name="create_access")
	public Boolean getCreate() {
		return create;
	}

	public void setCreate(Boolean create) {
		this.create = create;
	}

	@Column(name="copy_access")
	public Boolean getCopy() {
		return copy;
	}

	public void setCopy(Boolean copy) {
		this.copy = copy;
	}

	@Column(name="modify_access")
	public Boolean getModify() {
		return modify;
	}

	public void setModify(Boolean modify) {
		this.modify = modify;
	}

	@Column(name="print_access")
	public Boolean getPrint() {
		return print;
	}

	public void setPrint(Boolean print) {
		this.print = print;
	}

	@Column(name="delete_access")
	public Boolean getDelete() {
		return delete;
	}

	public void setDelete(Boolean delete) {
		this.delete = delete;
	}

	@Column(name="view_HR_access")
	public Boolean getViewHR() {
		return viewHR;
	}

	public void setViewHR(Boolean viewHR) {
		this.viewHR = viewHR;
	}

	@Column(name="isactive")
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Column(name="isapproved")
	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	@Column(name="grades")
	public String[] getGrades() {
		return grades;
	}

	public void setGrades(String[] grades) {
		this.grades = grades;
	}

	@Column(name="created_by")
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Column(name="created_On")
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Column(name="approved_by")
	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	@Column(name="approved_on")
	public Date getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Date approvedOn) {
		this.approvedOn = approvedOn;
	}

	@Column(name="endDate")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Column(name="isCustomRole")
	public Boolean getIsCustomRole() {
		return isCustomRole;
	}

	public void setIsCustomRole(Boolean isCustomRole) {
		this.isCustomRole = isCustomRole;
	}

	@Override
	public String toString() {
		return "Role [roleId=" + roleId + ", roleName=" + roleName + ", read=" + read + ", create=" + create + ", copy="
				+ copy + ", modify=" + modify + ", print=" + print + ", delete=" + delete + ", viewHR=" + viewHR
				+ ", isActive=" + active + ", isApproved=" + approved + ", grades=" + grades + ", createdBy="
				+ createdBy + ", createdOn=" + createdOn + ", approvedBy=" + approvedBy + ", approvedOn=" + approvedOn
			    + "]";
	}
	
}
