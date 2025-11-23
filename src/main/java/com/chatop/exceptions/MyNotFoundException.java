package com.chatop.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 404 not found
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class MyNotFoundException extends RuntimeException {

		private static final long serialVersionUID = 1L;
	    public MyNotFoundException() { }
		public MyNotFoundException(String message) { super(message); }
			
	}
