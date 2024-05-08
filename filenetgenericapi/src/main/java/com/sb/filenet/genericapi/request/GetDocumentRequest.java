package com.sb.filenet.genericapi.request;


import lombok.Data;

@Data
public class GetDocumentRequest {

    private String objectStore;
    private String username;
    private String password;
    private String documentClass;
    private String properties;
	public String getObjectStore() {
		return objectStore;
	}
	public void setObjectStore(String objectStore) {
		this.objectStore = objectStore;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getDocumentClass() {
		return documentClass;
	}
	public void setDocumentClass(String documentClass) {
		this.documentClass = documentClass;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
    
    
}
