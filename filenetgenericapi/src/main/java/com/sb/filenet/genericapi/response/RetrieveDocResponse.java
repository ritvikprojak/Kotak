package com.sb.filenet.genericapi.response;

import lombok.Data;

import java.io.InputStream;

@Data
public class RetrieveDocResponse {
    private InputStream fileContent;
    private String filebase64Content;
    private String fileName;
    private String mimeType;
	public InputStream getFileContent() {
		return fileContent;
	}
	public void setFileContent(InputStream fileContent) {
		this.fileContent = fileContent;
	}
	public String getFilebase64Content() {
		return filebase64Content;
	}
	public void setFilebase64Content(String filebase64Content) {
		this.filebase64Content = filebase64Content;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
    
    
}
