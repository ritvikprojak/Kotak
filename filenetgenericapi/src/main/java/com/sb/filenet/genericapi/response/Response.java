package com.sb.filenet.genericapi.response;

import lombok.Data;

@Data
public class Response {
    private String docId;
    private Status status;
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
    
    
}
