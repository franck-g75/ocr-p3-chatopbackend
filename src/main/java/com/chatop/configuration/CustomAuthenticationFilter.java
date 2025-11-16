package com.chatop.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.chatop.controllers.UserController;
import com.chatop.repositories.UserRepository;
import com.chatop.model.MyDbUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Class used to authenticate user (the first time with login and password)
 * not possible to use UserDetails loadUserByUsername because it's an email login form. there is no userName field
 */
@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {
	
	Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserRepository userRepo;
	
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	/**
	 * the login machine
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request,	HttpServletResponse response, FilterChain filterChain)
			  throws   BadCredentialsException, AuthenticationCredentialsNotFoundException {
		
		log.trace("CustomAuthenticationFilter.doFilterInternal(request,response,filterChain)...");
		
		
		//log.debug("FilterChain=" + filterChain.toString());
		
		//if (request.getRequestURI().contains("register") || request.getRequestURI().contains("messages") ||
		//		(request.getMethod()==HttpMethod.POST.toString())&&(request.getRequestURI().contains("rentals"))) {
		//	log.debug("register or messages or post.rentals URL user must be already authenticated");
		//	this.followFilterChain(request,response,filterChain);
		//} else {
		
		if (request.getRequestURI().contains("login")) {
			String bodyData=null;							//request body data contains JSON 
			String userLogin = "";							//the login in the request
			String userPwd = "";							//the password in the request
			MyDbUser user_db = null;								//the DB user found (or not) in the dataBase
			UsernamePasswordAuthenticationToken userToken = null;  //the authenticated user found
			
			// Récupération des données du body
			
			try {
				bodyData = request.getReader().lines().collect(Collectors.joining()); //read the JSON in the request
			} catch (IOException e) {
				log.trace("IOException : ".concat(e.toString()));
				followFilterChain(request, response, filterChain);
			}
			
			if ((bodyData!=null) && (bodyData.length()>0)) { 
	        	
	        	//transform JSON data in 2 strings
	        	BasicJsonParser parser = new BasicJsonParser();
    			try {
    	            Map<String, Object> result = parser.parseMap(bodyData);
    	            userLogin = (String) result.get("email");
    	            userPwd = (String) result.get("password");
    	        } catch (Exception e) {
    	            throw new AuthenticationCredentialsNotFoundException(e.getMessage());
    	        }
	        			
	        	log.info("login = " + userLogin);
	        	log.info("Authentification en cours... : ");//+ this.passwordEncoder().encode(requestBody.getPassword()));
	        	
	        	//find the user by his email in the database
	        	
	        	user_db= this.userRepo.findByEmail(userLogin);
	        	
	        	if (Objects.isNull(user_db)) {
	        		log.debug("User not found in DB");
	        		throw new AuthenticationCredentialsNotFoundException("User not found in DB");
	        	} else {
	        		if (user_db.getEmail().equals(userLogin) && 
	        			//user.getPassword().equals(this.passwordEncoder().encode(requestBody.getPassword())) &&
	        			passwordEncoder().matches( userPwd , user_db.getPassword() )
	        		) {
	        			userToken = authenticateAgainstThirdPartyAndGetAuthentication(userLogin, this.passwordEncoder().encode(userPwd));
	    			} else {
	    				log.debug("External system authentication failed");
	    				throw new AuthorizationDeniedException("External system authentication failed");
	    			}
	        	}

	        	SecurityContextHolder.getContext().setAuthentication(userToken); //authenticate the user 
	        	log.info("User authenticated following the filterChain");
	        	followFilterChain(request, response, filterChain);
	        	
			} else {
				
				log.info("empty request found for authentication , maybe a different filter will understand this request...");
				followFilterChain(request, response, filterChain);
				
			}
		} else {
			log.debug("not a login form : URL user must be already authenticated");
			this.followFilterChain(request,response,filterChain);
		}
    }
	
	/**
	 * Create the userToken
	 * @param name
	 * @param password
	 * @return
	 */
	private static UsernamePasswordAuthenticationToken authenticateAgainstThirdPartyAndGetAuthentication(String name, String password) {
        final List<SimpleGrantedAuthority> grantedAuths = new ArrayList<>();
        //grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
        final UserDetails principal = new User(name, password, grantedAuths);
        return new UsernamePasswordAuthenticationToken(principal, password, grantedAuths);
    }
	
	/**
	 * follow the filterChain (it's like a return for doFilter)
	 * @param request
	 * @param response
	 * @param filterChain
	 */
	private void followFilterChain(
			HttpServletRequest request, 
			HttpServletResponse response, 
			FilterChain filterChain) {
		try {
			filterChain.doFilter(request, response);
		} catch (IOException | ServletException e) {
			log.trace("Following FilterChain".concat(e.toString()));
		}
	}

}
