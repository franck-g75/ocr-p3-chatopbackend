package com.chatop.services;

import java.sql.Timestamp;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import com.chatop.exceptions.MyDbException;
import com.chatop.exceptions.MyNotFoundException;
import com.chatop.exceptions.MyWebInfoException;
import com.chatop.exceptions.MyWebNullException;
import com.chatop.model.MyDbUser;
import com.chatop.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@Service
public class UserService {
	
	Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	UserRepository userRepo;
	
	/**
	 * create a user and save it in DB
	 * @param email
	 * @param name
	 * @param hash
	 * @return null if DB not able to save the user
	 * @throws MyWebNullException if 1 field is null
	 * @throws MyWebInfoException if 1 field is empty
	 */
	public MyDbUser saveUser(String email, String name, String hash) {
		
		MyDbUser myDbUser = new MyDbUser();
		
		myDbUser.setCreated_at( Timestamp.from(Instant.now()) );
    	myDbUser.setUpdated_at( Timestamp.from(Instant.now()) );
    	myDbUser.setEmail( email );
    	myDbUser.setName( name );
    	myDbUser.setPassword( hash );
    	
    	try {
    		myDbUser = userRepo.save(myDbUser);
    	} catch (CannotCreateTransactionException ccte) {
			log.error( "CannotCreateTransactionException " + ccte.getMessage() );
			throw new MyDbException("CannotCreateTransactionException"); //don't show user database structure...
     	} catch (ConstraintViolationException cve) {
     		log.error("ConstraintViolationException : " + cve.getMessage());
    		throw new MyWebInfoException("ConstraintViolationException violation de contraintes (champs trop longs ? ou trop courts ?)"); //don't show user database structure...
     	} catch (Exception e) { 
    		log.error("Exception : " + e.getMessage());
    		throw new MyDbException("Exception"); //don't show user database structure...
    	}
    	
    	return myDbUser;
	}
	
	/**
	 * 
	 * @param email
	 * @return
	 */
	public MyDbUser findByEmail(String email) {
		
		MyDbUser retour = null;
		
		try {
			retour = userRepo.findByEmail(email);
		} catch (CannotCreateTransactionException ccte) {
			log.error("user " + email + " not found " + ccte.getMessage() + " " + ccte.toString());
			throw new MyDbException("user " + email + " not found " + ccte.getMessage() + " " + ccte.toString());
		} catch (EntityNotFoundException enfe) {
			log.error("user " + email + " not found " + enfe.getMessage() + " " + enfe.toString());
			throw new MyNotFoundException("user not found email=" + email);
		} catch (Exception e) {
			log.error("user " + email + " not found " + e.getMessage() + " " + e.toString());
			throw new MyNotFoundException("user " + email + " not found");
		}
		
		return retour;
	}
	
	/**
	 * 
	 * @param id
	 * @return 
	 */
	public MyDbUser getById(Integer id) {
		
		MyDbUser retour = null;
		
		try {
			retour = userRepo.getById(id);
		} catch (CannotCreateTransactionException ccte) {
			log.error("user " + id.toString() + " not found " + ccte.getMessage() + " " + ccte.toString());
			throw new MyDbException("user " + id.toString() + " not found " + ccte.getMessage() + " " + ccte.toString());
		} catch (EntityNotFoundException enfe) {
			log.error("user " + id.toString() + " not found " + enfe.getMessage() + " " + enfe.toString());
			throw new MyNotFoundException("user not found id=" + id.toString());
		} catch (Exception e) {
			log.error("user " + id.toString() + " not found " + e.getMessage() + " " + e.toString());
			throw new MyNotFoundException("user " + id.toString() + " not found " + e.getMessage() + " " + e.toString());
		}
		
		return retour;

	}
	
}
