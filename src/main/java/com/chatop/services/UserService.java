package com.chatop.services;

import java.sql.Timestamp;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chatop.controllers.UserController;
import com.chatop.exceptions.MyDbException;
import com.chatop.exceptions.MyWebInfoException;
import com.chatop.exceptions.MyWebNullException;
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
	public MyDbUser saveUser(String email, String name, String hash) throws MyDbException{
		
		MyDbUser myDbUser = new MyDbUser();
		
		myDbUser.setCreated_at( Timestamp.from(Instant.now()) );
    	myDbUser.setUpdated_at( Timestamp.from(Instant.now()) );
    	myDbUser.setEmail( email );
    	myDbUser.setName( name );
    	myDbUser.setPassword( hash );
    	
    	try {
    		userRepo.save(myDbUser);
    	} catch (Exception e) { 
    		log.error("MyDbException : " + e.getMessage());
    		throw new MyDbException("MyDbException : " + e.getMessage()); 
    	}
    	
    	return myDbUser;
	}
	
	/**
	 * 
	 * @param email
	 * @return
	 */
	public MyDbUser findByEmail(String email) {
		return userRepo.findByEmail(email);
	}
	
	/**
	 * 
	 * @param id
	 * @return 
	 */
	public MyDbUser getById(Integer id) {
		return userRepo.getById(id);
	}
	
}
