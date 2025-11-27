package com.chatop.exceptions;

/**
 * very useful to display the exceptions in GlobalExceptionHandler
 */
public class ErrorDetails {

	private Integer status;
	private String message;
	private String statusMsg;
	
	public ErrorDetails(Integer status, String statusMsg, String msg) {
		super();
		this.message = msg;
		this.statusMsg = statusMsg;
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}

	public String getStatusMsg() {
		return statusMsg;
	}
	
	public String getMessage() {
		return message;
	}

	public String toString() {
		return status.toString() + " " + statusMsg + " " + message;
	}
}
