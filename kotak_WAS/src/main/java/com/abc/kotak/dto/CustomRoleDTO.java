package com.abc.kotak.dto;

import java.util.List;

/**
 * @author Anup
 *
 */
public class CustomRoleDTO {
	private List<String> responsiblity;
	private String inclusiveORexclusive;
	
	public CustomRoleDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<String> getResponsiblity() {
		return responsiblity;
	}

	public void setResponsiblity(List<String> responsiblity) {
		this.responsiblity = responsiblity;
	}

	public String getInclusiveORexclusive() {
		return inclusiveORexclusive;
	}

	public void setInclusiveORexclusive(String inclusiveORexclusive) {
		this.inclusiveORexclusive = inclusiveORexclusive;
	}

	@Override
	public String toString() {
		return "CustomRoleDTO [responsiblity=" + responsiblity
				+ ", inclusiveORexclusive=" + inclusiveORexclusive + "]";
	}
		
}
