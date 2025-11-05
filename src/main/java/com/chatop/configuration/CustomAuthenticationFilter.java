package com.chatop.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.chatop.controllers.LoginController;
import com.nimbusds.jose.shaded.gson.Gson;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {
	
	Logger log = LoggerFactory.getLogger(LoginController.class);

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, 
			HttpServletResponse response, 
			FilterChain filterChain)
			throws   BadCredentialsException, AuthenticationCredentialsNotFoundException {
		
		log.trace("CustomAuthenticationFilter.doFilterInternal(request,response,filterChain)...");
		
		CustomRequestBody requestBody = null;
		
		// Récupération des données du body
		
        String bodyData=null;
		try {
			bodyData = request.getReader().lines().collect(Collectors.joining());
		} catch (IOException e) {
			log.trace("CustomAuthenticationFilter1".concat(e.toString()));
			try {
				filterChain.doFilter(request, response);
			} catch (IOException | ServletException e1) {
				log.trace("CustomAuthenticationFilter2".concat(e1.toString()));
			}
		}
        if ((bodyData!=null)&& (bodyData.length()>0)) { 
        	//transformer les données de JSON à CustomRequestBody
        	requestBody = new Gson().fromJson(bodyData, CustomRequestBody.class);
        	log.info("requestBody=" + requestBody.toString());
        	log.info("Authentification en cours...");
        	UsernamePasswordAuthenticationToken ut = null;
        	if ("test@test.com".equals(requestBody.getEmail()) && "test!31".equals(requestBody.getPassword())) {
        		ut = authenticateAgainstThirdPartyAndGetAuthentication(requestBody.getEmail(), requestBody.getPassword());
			} else {
				throw new BadCredentialsException("External system authentication failed");
			}
        	
        	SecurityContextHolder.getContext().setAuthentication(ut); 
        	log.info("User unique authentifé. descente de la chaine");
        	try {
				filterChain.doFilter(request, response);
			} catch (IOException | ServletException e) {
				log.trace("CustomAuthenticationFilter3".concat(e.toString()));
			}
        } else {// nothing n the request body (can be normal...)
        	log.info("maybe a different filter will understand this request...");
    		try {
				filterChain.doFilter(request, response);
			} catch (IOException | ServletException e) {
				log.trace("CustomAuthenticationFilter4".concat(e.toString()));
			}
        }
    }
	
	private static UsernamePasswordAuthenticationToken authenticateAgainstThirdPartyAndGetAuthentication(String name, String password) {
        final List<SimpleGrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
        final UserDetails principal = new User(name, password, grantedAuths);
        return new UsernamePasswordAuthenticationToken(principal, password, grantedAuths);
    }

}
