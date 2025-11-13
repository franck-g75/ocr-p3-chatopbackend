package com.chatop.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(value=MyDbException.class)
	public ResponseEntity<ErrorDetails> myDbException(MyDbException ex) {
		ErrorDetails errorModel = new ErrorDetails(500, ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value=MyWebNullException.class)
	public ResponseEntity<ErrorDetails> myWebNullException(MyWebNullException ex) {
		ErrorDetails errorModel = new ErrorDetails(406, ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler(value=MyWebInfoException.class)
	public ResponseEntity<ErrorDetails> myWebNullException(MyWebInfoException ex) {
		ErrorDetails errorModel = new ErrorDetails(406, ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.NOT_ACCEPTABLE);
	}
	
	
	@ExceptionHandler(value=AuthenticationCredentialsNotFoundException.class)
	public ResponseEntity<ErrorDetails> myWebNullException(AuthenticationCredentialsNotFoundException ex) {
		ErrorDetails errorModel = new ErrorDetails(401, ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(value=AuthorizationDeniedException.class)
	public ResponseEntity<ErrorDetails> myWebNullException(AuthorizationDeniedException ex) {
		ErrorDetails errorModel = new ErrorDetails(401, ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.FORBIDDEN);
	}
	
}
