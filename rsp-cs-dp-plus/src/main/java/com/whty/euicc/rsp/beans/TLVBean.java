package com.whty.euicc.rsp.beans;

public class TLVBean {
	private String taglen;
	private String value;
	
	public TLVBean(String taglen, String value) {
		super();
		this.taglen = taglen;
		this.value = value;
	}
	public String getTaglen() {
		return taglen;
	}
	public void setTaglen(String taglen) {
		this.taglen = taglen;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
