package com.xunwei.collectdata.utils;

public class ErrorInfo {
	private int errorNumber;
	private String description;
	
	public static final int SUCCESS = 0;
	public static final int FAIL = -1;
	
	//JSON keys
	public static final String errNum = "/errorNumber";
	public static final String errDescription = "/description";
	
	public int getErrorNumber() {
		return errorNumber;
	}
	
	public void setErrorNumber(int errorNumber) {
		this.errorNumber = errorNumber;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
