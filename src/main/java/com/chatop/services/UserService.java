package com.chatop.services;

import java.sql.Timestamp;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import com.chatop.exceptions.MyDbException;
import com.chatop.exceptions.MyNotFoundException;
import com.chatop.exceptions.MyWebInfoException;
import com.chatop.model.MyDbUser;
import com.chatop.repositories.UserRepository;

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
	public MyDbUser saveUser(String email, String name, String hash) throws MyDbException {
		
		//email format and length field checked in the model annotations
		
		MyDbUser myDbUser = new MyDbUser();
		
		myDbUser.setCreated_at( Timestamp.from(Instant.now()) );
    	myDbUser.setUpdated_at( Timestamp.from(Instant.now()) );
    	myDbUser.setEmail( email );
    	myDbUser.setName( name );
    	myDbUser.setPassword( hash );
    	
    	try {
    		myDbUser = userRepo.save(myDbUser);
    	} catch (CannotCreateTransactionException ex) {
			throw new CannotCreateTransactionException("DB connection not avaiable...");
		} catch (Exception e) { 
    		log.error("saveUser MyDbException : " + e.getMessage());
    		throw new MyDbException("saveUser : MyDbException"); //don't show user database structure...
    	}
		
    	return myDbUser;
	}
	
	
	
	/**
	 * find MyDbUser by email
	 * @param email
	 * @return 
	 * @throws MyDbException
	 * @throws MyWebInfoException
	 */
	public MyDbUser findByEmail(String email) throws DataAccessResourceFailureException,MyNotFoundException {
		
		//email checking... not necessary : done at the entry of user by email annotation in the model
		
		MyDbUser retour = null;
		
		try {
			retour = userRepo.findByEmail(email);
		} catch (DataAccessResourceFailureException ex) {
			throw new DataAccessResourceFailureException("DB connection not avaiable.");
		} catch (Exception e) {
			log.error("user " + email + " not found " + e.getMessage() + " " + e.toString());
			throw new MyNotFoundException("user " + email + " not found");
		}
		
		return retour;
	}
	
	
	
	
	
	/**
	 * Get MyDbUser by id
	 * @param id
	 * @return the MyDbUser object
	 */
	public MyDbUser getById(Integer id) throws CannotCreateTransactionException, MyNotFoundException {
		
		MyDbUser retour = null;
		try {
			retour = userRepo.getById(id);
		} catch (CannotCreateTransactionException ex) {
			throw new CannotCreateTransactionException("DB connection not avaiable...");
		} catch (Exception e) {
			log.error("user " + id.toString() + " not found " + e.getMessage() + " " + e.toString());
			throw new MyNotFoundException("user " + id + " not found");
		}
		return retour;
		
	}

	
	
}
