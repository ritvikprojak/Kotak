//package com.abc.kotak.dto;
//
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//
//import org.hibernate.annotations.NotFound;
//import org.hibernate.annotations.NotFoundAction;
//
//import com.abc.kotak.model.Documents;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//public class DocumentDTO {
//
//	private Long id;
//	private String documentName;
//	private Boolean isActive;
//	private String parent;
//	
//	public DocumentDTO() {
//		
//	}
//	
//	public DocumentDTO(Long id, String documentName, Boolean isActive, String parent) {
//		super();
//		this.id = id;
//		this.documentName = documentName;
//		this.isActive = isActive;
//		this.parent = parent;
//	}
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	public String getDocumentName() {
//		return documentName;
//	}
//	public void setDocumentName(String documentName) {
//		this.documentName = documentName;
//	}
//	public Boolean getIsActive() {
//		return isActive;
//	}
//	public void setIsActive(Boolean isActive) {
//		this.isActive = isActive;
//	}
//	public String getParent() {
//		return parent;
//	}
//	public void setParent(String parent) {
//		this.parent = parent;
//	}
//	
//	@Override
//	public String toString() {
//		return "DocumentDTO [id=" + id + ", documentName=" + documentName + ", isActive=" + isActive + ", parent="
//				+ parent + "]";
//	}
//	
//	
//	
//	
//}
