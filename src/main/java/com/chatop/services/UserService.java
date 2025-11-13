package com.chatop.services;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chatop.model.MyDbUser;
import com.chatop.repositories.UserRepository;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRepo;
	
	/**
	 * 
	 * @param email
	 * @param name
	 * @param hash
	 */
	public MyDbUser saveUser(String email, String name, String hash) {
		
		//Vérification des null
		
    	//Vérification des tailles 
    	
    	MyDbUser myDbUser = new MyDbUser();
    	myDbUser.setCreated_at( Timestamp.from(Instant.now()) );
    	myDbUser.setUpdated_at( Timestamp.from(Instant.now()) );
    	myDbUser.setEmail( email );
    	myDbUser.setName( name );
    	myDbUser.setPassword( hash );
    	
    	userRepo.save(myDbUser);
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
