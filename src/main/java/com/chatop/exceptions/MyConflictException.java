package com.chatop.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 409 conflict
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class MyConflictException extends RuntimeException {

	private static final long serialVersionUID = 1L;
    public MyConflictException() { }
	public MyConflictException(String message) { super(message); }
	
}
