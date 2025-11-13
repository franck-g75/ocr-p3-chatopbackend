package com.chatop.controllers;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.chatop.configuration.CustomUserRequestBody;
import com.chatop.exceptions.MyDbException;
import com.chatop.exceptions.MyWebInfoException;
import com.chatop.exceptions.MyWebNullException;
import com.chatop.model.MyDbUser;
import com.chatop.services.JWTService;
import com.chatop.services.UserService;

/**
 * 
 */
@RestController
public class UserController {
	
	Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
    private JWTService jwtService;
	
	@Autowired 
	UserService userService;
	
    /**
     * athentication mapping - used to create a token
     * @param authentication
     * @return
     */
    @PostMapping("/api/auth/login")//, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postLogin( Authentication authentication ) {
    	   log.info("api-auth-login path reached !");
    	   if (authentication != null) log.info("api-auth-login authentication n'est pas null !");
           String token = jwtService.generateToken(authentication);
           log.info("api-auth-login path return token");
           return "{\"token\":\"".concat(token).concat("\"}"); 
    }
    
    /**
     * get the user connected informations
     * @param authentication
     * @return
     */
    @GetMapping("/api/auth/me")
    public String getUserConnectedInfo(Authentication authentication) {
    	//error 400 if no token or 200 if ok
    	log.info("api-auth-me path reached !   " + authentication.getName());//retourne l'email);
       	
    	MyDbUser myDbUser = userService.findByEmail(authentication.getName());
    	return myDbUser.toJson();
    	
    }
    
    /**
     * get user by id
     * @return the user informations identified by the user id in the url
     */
    @GetMapping("/api/user/{id}")
    public String getUserById(Authentication authentication, @PathVariable Integer id) {
    	
    	//log.info("getUserById(" + id.toString() + ")");
    	
     	//looking for the  user in DB
    	MyDbUser myUser = userService.getById(id);
    	
    	//log.info("returning : " +  myUser.toJson() );
    	
    	//returning the user
    	return myUser.toJson();
    	
    }
    
    /**
     * New user
     * @param authentication
     * @return registered if OK or empty if not
     */
    @PostMapping("/api/auth/register")
    public String postNewUser(@RequestBody CustomUserRequestBody requestBody) {
    	    	
    	log.info("CustomRequestBody = " + requestBody.toString() + ")");
    	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());
    	String shaSalted = bCryptPasswordEncoder.encode( requestBody.getPassword());
    	
    	MyDbUser myDbUser = null;
    	
    	//null Checking
		if ((requestBody.getEmail()==null) || (requestBody.getName()==null) || (requestBody.getPassword()==null)) {
			throw new MyWebNullException("Null is not permitted to save a user");
		} else {
			//size checking  
			if ((requestBody.getEmail().length()<3) || (requestBody.getName().length()<3) || (requestBody.getPassword().length()<3)) {
				throw new MyWebInfoException("field length <3 is not permitted to save a user");
			} else {
				myDbUser = userService.saveUser( requestBody.getEmail(), requestBody.getName(), shaSalted );
    	   		log.info("myDbUser just created = " + myDbUser.toJson());
			}
		}
		
		//user is created, if not : an exception was thrown
    	//user authentication 
    	final List<SimpleGrantedAuthority> grantedAuths = new ArrayList<>(); //empty list
        final UserDetails principal = new User(requestBody.getEmail(), shaSalted, grantedAuths);
        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(principal, shaSalted, grantedAuths);
        SecurityContextHolder.getContext().setAuthentication(userToken); //authenticate the user 
        
        //token creation
        String token = jwtService.generateToken(userToken);
        
        //return the token
        return "{\"token\":\"".concat(token).concat("\"}"); 
    }
}
