package com.abc.kotak.model;


public class InputObj {

	@Override
	public String toString() {
		return "InputObj [text=" + text + ", key=" + key + ", nBits=" + nBits + "]";
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getnBits() {
		return nBits;
	}
	public void setnBits(int nBits) {
		this.nBits = nBits;
	}
	public InputObj(String text, String key, int nBits) {
		super();
		this.text = text;
		this.key = key;
		this.nBits = nBits;
	}
	public InputObj() {
		super();
		// TODO Auto-generated constructor stub
	}
	private String text;
	private String key;
	private int nBits;
	
	
	
}
