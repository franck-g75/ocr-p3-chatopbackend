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

import com.chatop.configuration.CustomRequestBody;
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
    public String postNewUser(@RequestBody CustomRequestBody requestBody) {
    	//error 400 if no token or 200 if ok
    	
    	log.info("CustomRequestBody = " + requestBody.toString() + ")");
    	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());
    	
    	//no field check here
    	//create the user in DB
    	try {
    		MyDbUser myDbUser = userService.saveUser( requestBody.getEmail(), requestBody.getName(), bCryptPasswordEncoder.encode( requestBody.getPassword()) ) ;
    		log.info("myDbUser just created = " + myDbUser.toJson());
    	} catch (Exception e) {
    		log.error("postNewUser : " + e.toString());
    	}
    	
    	//user authentification 
    	final List<SimpleGrantedAuthority> grantedAuths = new ArrayList<>(); //empty list
        final UserDetails principal = new User(requestBody.getEmail(), bCryptPasswordEncoder.encode( requestBody.getPassword()), grantedAuths);
        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(principal, bCryptPasswordEncoder.encode( requestBody.getPassword()), grantedAuths);
        SecurityContextHolder.getContext().setAuthentication(userToken); //authenticate the user 
        
        //token creation
        String token = jwtService.generateToken(userToken);
        
        //return the token
        return "{\"token\":\"".concat(token).concat("\"}"); 
    }
}
