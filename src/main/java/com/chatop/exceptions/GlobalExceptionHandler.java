package com.chatop.exceptions;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	//Exceptions thrown in chatopbackend code (in a if statement)
	
	//exception raised when user infos are wrong (bad format, too long, too short...)
	@ExceptionHandler(value=MyWebInfoException.class)
	public ResponseEntity<ErrorDetails> myWebInfoException(MyWebInfoException ex) {
		ErrorDetails errorModel = new ErrorDetails(400, "BAD_REQUEST", ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.BAD_REQUEST);
	}
	
	//exception raised when user try to add an forbidden existing data (a new user with an existing email)
	@ExceptionHandler(value=MyConflictException.class)
	public ResponseEntity<ErrorDetails> myConflictException(MyConflictException ex) {
		ErrorDetails errorModel = new ErrorDetails(409, "CONFLICT", ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.CONFLICT);
	}
	
	//exception raised when an object is not found
	@ExceptionHandler(value=MyNotFoundException.class)
	public ResponseEntity<ErrorDetails> myNotFoundException(MyNotFoundException ex) {
		ErrorDetails errorModel = new ErrorDetails(404, "NOT_FOUND", ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.NOT_FOUND);
	}
	
	//exception raised in case something goes wrong in the database an not raised by system exceptions 
	@ExceptionHandler(value=MyDbException.class)
	public ResponseEntity<ErrorDetails> myDbException(MyDbException ex) {
		ErrorDetails errorModel = new ErrorDetails(500, "INTERNAL_SERVER_ERROR", ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	
	
	//Exceptions thrown by the system
	//they are catched before the catch statement in chatopBackEnd code
	
	//MalformedURLException
	@ExceptionHandler(value=MalformedURLException.class)
	public ResponseEntity<ErrorDetails> malformedURLException(MalformedURLException ex) {
		ErrorDetails errorModel = new ErrorDetails(500, "MalformedURLException", ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	//everything is ok but nothing found in the DB
	@ExceptionHandler(value=EntityNotFoundException.class)
	public ResponseEntity<ErrorDetails> entityNotFoundException(EntityNotFoundException ex) {
		ErrorDetails errorModel = new ErrorDetails(404, "NOT_FOUND", ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.NOT_FOUND);
	}
	
	//String in an integer field
	@ExceptionHandler(value=MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorDetails> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		ErrorDetails errorModel = new ErrorDetails(400, "BAD_REQUEST", ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.BAD_REQUEST);
	}
	
	//DB is off
	@ExceptionHandler(value=CannotCreateTransactionException.class)
	public ResponseEntity<ErrorDetails> cannotCreateTransactionException(CannotCreateTransactionException ex) {
		ErrorDetails errorModel = new ErrorDetails(500, "INTERNAL_SERVER_ERROR", ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	//the URL does not match any authorized request or something is not found in DB
	@ExceptionHandler(value=NoResourceFoundException.class)
	public ResponseEntity<ErrorDetails> noResourceFoundException(NoResourceFoundException ex) {
		ErrorDetails errorModel = new ErrorDetails(403, "FORBIDDEN", ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.FORBIDDEN);
	}
	
	//INTERNAL_SERVER_ERROR
	@ExceptionHandler(value=RuntimeException.class)
	public ResponseEntity<ErrorDetails> runtimeException(RuntimeException ex) {
		ErrorDetails errorModel = new ErrorDetails(500, "INTERNAL_SERVER_ERROR", ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	//the login and the password don't match any user login and password
	@ExceptionHandler(value=AuthorizationDeniedException.class)
	public ResponseEntity<ErrorDetails> authorizationDeniedException(AuthorizationDeniedException ex) {
		ErrorDetails errorModel = new ErrorDetails(401, "UNAUTHORIZED", ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.UNAUTHORIZED);
	}
	
	//URISyntaxException
	@ExceptionHandler(value=URISyntaxException.class)
	public ResponseEntity<ErrorDetails> uRISyntaxException(URISyntaxException ex) {
		ErrorDetails errorModel = new ErrorDetails(500, "INTERNAL_SERVER_ERROR", ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	//MaxUploadSizeExceededException
	@ExceptionHandler(value=MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorDetails> maxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
		ErrorDetails errorModel = new ErrorDetails(413, "PAYLOAD_TOO_LARGE", ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.PAYLOAD_TOO_LARGE);
	}
	
	//JsonProcessingException
	@ExceptionHandler(value=JsonProcessingException.class)
	public ResponseEntity<ErrorDetails> jsonProcessingException(JsonProcessingException ex) {
		ErrorDetails errorModel = new ErrorDetails(500, "JsonProcessingException", ex.getMessage());
		return new ResponseEntity<ErrorDetails>  (errorModel, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
}
