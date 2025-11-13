package com.chatop.exceptions;

public class ErrorDetails {

	private Integer status;
	private String message;
	
	public ErrorDetails(Integer status, String msg) {
		super();
		this.message = msg;
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

}
