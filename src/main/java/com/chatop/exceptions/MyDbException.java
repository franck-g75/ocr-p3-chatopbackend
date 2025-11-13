package com.chatop.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class MyDbException extends RuntimeException {

	private static final long serialVersionUID = 1L;
    public MyDbException() { }
	public MyDbException(String message) { super(message); }
	
}
