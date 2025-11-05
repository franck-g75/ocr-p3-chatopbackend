package com.chatop.services;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Service
public class JWTService {

	@Value("${jwt.public.key}")
	RSAPublicKey key;

	@Value("${jwt.private.key}")
	RSAPrivateKey priv;
	
	Logger log = LoggerFactory.getLogger(JWTService.class);
	
    public JWTService() {
    }
    
    //Cours + IA
    public String generateToken(Authentication authentication) {
	    Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                   .issuer("self")
                   .issuedAt(now)
                   .expiresAt(now.plus(1, ChronoUnit.HOURS))
                   .subject(authentication.getName())
                   .build();
        log.info("Token généré à " + now.toString());
        return this.jwtEncoder().encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    //IA + FG
	 public String getUsernameFromToken(String token) {
		 log.info("getUsernameFromToken...");
		 String retour =  this.jwtDecoder().decode(token).getSubject();
		 log.info(retour);
		 return retour;
	 }
	 
	 //IA + FG
	 private Instant getExpirationDateFromToken(String token) {
		 log.info("getExpirationDateFromToken...");
		 Instant retour =  this.jwtDecoder().decode(token).getExpiresAt();
		 log.info(retour.toString());
		 return retour;
	 }	
	 
	 //IA + FG
	 public boolean validateToken(String token, UserDetails userDetails) {
	     final String username = getUsernameFromToken(token);
	     return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	 }
	
	 //IA + FG
	 private boolean isTokenExpired(String token) {
	     final Instant expiration = getExpirationDateFromToken(token);
	     return expiration.isBefore(Instant.now());
	 }
	  /**
	   * from https://github.com/spring-projects/spring-security-samples/blob/main/servlet/spring-boot/java/jwt/login/src/main/java/example/RestConfig.java
	   * @return 
	   */
     @Bean
	 JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(this.key).build();
	 }

     /**
      * from https://github.com/spring-projects/spring-security-samples/blob/main/servlet/spring-boot/java/jwt/login/src/main/java/example/RestConfig.java
      */ 
	 @Bean
	 JwtEncoder jwtEncoder() {
		JWK jwk = new RSAKey.Builder(this.key).privateKey(this.priv).build();
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
   	 }
	  
}
    
    
    
    
    
    