package com.abc.kotak.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.abc.kotak.dto.EntityIdResolver;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="Documents",schema = "HRUPM")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
property = "id",
resolver = EntityIdResolver.class,
scope=Documents.class)
public class Documents implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "staging")
	@SequenceGenerator(allocationSize = 1,name = "staging",sequenceName = "staging")
	@Column(name = "docid")
	private Long id;
	private String documentName;
	private Boolean isActive;
	
	@NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JsonIgnore
	@JoinColumn(name="parent_id")
	private Documents parent;
	
    @OneToMany(mappedBy="parent")
    @JsonIgnore
    private List<Documents> children;
    
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "Doc_Name")
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	
	public Documents getParentId() {
		return parent;
	}
	public void setParentId(Documents parentId) {
		this.parent = parentId;
	}
		
	public List<Documents> getChildren() {
		return children;
	}
	public void setChildren(List<Documents> children) {
		this.children = children;
	}
	
	@Column(name="isActive")
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result
				+ ((documentName == null) ? 0 : documentName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((isActive == null) ? 0 : isActive.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Documents other = (Documents) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (documentName == null) {
			if (other.documentName != null)
				return false;
		} else if (!documentName.equals(other.documentName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isActive == null) {
			if (other.isActive != null)
				return false;
		} else if (!isActive.equals(other.isActive))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Documents [id=" + id + ", documentName=" + documentName + ", parentId=" + parent + "]";
	}
	
}
