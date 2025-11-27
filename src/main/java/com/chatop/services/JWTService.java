package com.chatop.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import com.chatop.repositories.UserRepository;

/**
 * used to manage the bearer token
 */
@Service
public class JWTService {

	Logger log = LoggerFactory.getLogger(JWTService.class);
	
    public JWTService() {}
    
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	JwtEncoder jwtEncoder;
	
	@Autowired
	JwtDecoder jwtDecoder;
    
    
    /**
     * generate the token
     * @param authentication
     * @return the token
     */
    public String generateToken(Authentication authentication) {
	    Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                   .issuer("self")
                   .issuedAt(now)
                   .expiresAt(now.plus(30, ChronoUnit.MINUTES))
                   .subject(authentication.getName())
                   .build();
        log.info("Token generated at " + now.toString());
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    

    
    /**
     * getUsernameFromToken
     * @param token
     * @return
     */
	 public String getUsernameFromToken(String token) {
		 log.info("getUsernameFromToken...");
		 String retour =  this.jwtDecoder.decode(token).getSubject();
		 log.info(retour);
		 return retour;
	 }
	 
	 
	 
	 
	 /**
	  * getExpirationDateFromToken
	  * @param token
	  * @return 
	  */
	 private Instant getExpirationDateFromToken(String token) {
		 log.info("getExpirationDateFromToken...");
		 Instant retour =  this.jwtDecoder.decode(token).getExpiresAt();
		 log.info(retour.toString());
		 return retour;
	 }	
	 
	 
	 /**
	  * validateToken
	  * @param token
	  * @param userDetails
	  * @return
	  */
	 public boolean validateToken(String token, UserDetails userDetails) {
	     final String username = getUsernameFromToken(token);
	     return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	 }
	
	 
	 
	 /**
	  * isTokenExpired
	  * @param token
	  * @return
	  */
	 private boolean isTokenExpired(String token) {
	     final Instant expiration = getExpirationDateFromToken(token);
	     return expiration.isBefore(Instant.now());
	 }
 

}
    
    
    
    
    
    