package com.sb.filenet.genericapi.request;


import lombok.Data;

@Data
public class Request {

    private String objectStore;
    private String username;
    private String password;
    private String docId;
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
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
    
    
}
