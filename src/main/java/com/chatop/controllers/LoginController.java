package com.chatop.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.chatop.services.JWTService;

/**
 * 
 */
@RestController
public class LoginController {
	
	//attention me est appelé en premier puis appel de login 
	
	Logger log = LoggerFactory.getLogger(LoginController.class);
	
    private JWTService jwtService;
   
    public LoginController(JWTService jwtService) {
        this.jwtService = jwtService;
    }
    

    /**
     * 
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
     * New user
     * @param authentication
     * @return
     */
    @PostMapping("/auth/register")//avec le projet angular
    public String postRegister(Authentication authentication) {
    	//error 400 if no token or 200 if ok
    	return "registered";
    }
    
    /**
     * 
     * @return 
     */
    @GetMapping("/api/auth/me")//
    public String getMe() {
    	//error 400 if no token or 200 if ok
    	log.info("api-auth-me path reached !");
   	
    	return "{\r\n"
    			+ "  \"id\": 1,\r\n"
    			+ "	\"name\": \"Test TEST\",\r\n"
    			+ "	\"email\": \"test@test.com\",\r\n"
    			+ "	\"created_at\": \"2022/02/02\",\r\n"
    			+ "	\"updated_at\": \"2022/08/02\"  \r\n"
    			+ "}";
    }
    
}
