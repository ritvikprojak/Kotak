/*package com.abc.kotak.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;


*//**
 * @author qcc
 *
 *//*
@Entity
@Table(name="Role_Document_Mapping")
public class RoleToDocumentMapping implements Serializable {
	
	*//**
	 * 
	 *//*
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@JoinColumn(foreignKey=@ForeignKey(name="roleid"))
	private Long roleId;
	
	@JoinColumn(foreignKey=@ForeignKey(name="docid"))
	private Long docId;
	private String createdBy;
	private Date createON;
	
	

	public RoleToDocumentMapping() {
	}

	
	
	public RoleToDocumentMapping(Long roleId, Long docId, String createdBy,
			Date createON) {
		super();
		this.roleId = roleId;
		this.docId = docId;
		this.createdBy = createdBy;
		this.createON = createON;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="roleid")
	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	@Column(name="docid")
	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreateON() {
		return createON;
	}

	public void setCreateON(Date createON) {
		this.createON = createON;
	}

	@Override
	public String toString() {
		return "RoleToDocumentMapping [id=" + id + ", roleId=" + roleId
				+ ", docId=" + docId + "]";
	}
	
	
}
*/