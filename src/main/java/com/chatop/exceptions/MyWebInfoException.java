package com.chatop.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 400 bad request
 * When something is wrong in the body request (field with wrong size or missing)
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MyWebInfoException extends RuntimeException {

	private static final long serialVersionUID = 1L;
    public MyWebInfoException() { }
	public MyWebInfoException(String message) { super(message); }

}
