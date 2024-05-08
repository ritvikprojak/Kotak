package com.sb.filenet.genericapi.response;

import java.util.List;

import lombok.Data;

@Data
public class RetrieveDocsResponse {
	
	private List<RetrieveDocResponse> RetrieveDocResponse;  
	private Status status;
	public List<RetrieveDocResponse> getRetrieveDocResponse() {
		return RetrieveDocResponse;
	}
	public void setRetrieveDocResponse(List<RetrieveDocResponse> retrieveDocResponse) {
		RetrieveDocResponse = retrieveDocResponse;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

}
