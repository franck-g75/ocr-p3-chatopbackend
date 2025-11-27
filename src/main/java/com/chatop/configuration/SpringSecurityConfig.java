package com.chatop.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

	Logger log = LoggerFactory.getLogger(SpringSecurityConfig.class);

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
        return http
        	.csrf((csrf) -> csrf.ignoringRequestMatchers("/api/auth/login","/api/auth/register")) 	//site with no CSRF protection only on these endpoint
    	   	.oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))				//token OAuth2 filter
         	.authorizeHttpRequests(   (authorize) -> authorize										//all the end point must be securised except register image and swagger
				.requestMatchers(HttpMethod.GET, "/api/auth/me", "/api/user/**", "/api/rentals", "/api/rentals/**" ).authenticated()
				.requestMatchers(HttpMethod.POST, "/api/rentals", "/api/messages").authenticated()
				.requestMatchers(HttpMethod.PUT, "/api/rentals/**").authenticated()
				.requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()							//no connections needed for this end point
				.requestMatchers(HttpMethod.GET, "/image/**", "/swagger-ui/**", "/v3/**").permitAll()    )  //no connections needed for these end point
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   //session stateless
            .exceptionHandling((exceptions) -> exceptions											// popups //found on gitHub https://github.com/spring-projects/spring-security-samples
					.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())			// popups protection
					.accessDeniedHandler(new BearerTokenAccessDeniedHandler())
					)
            .build();
    }
	

}