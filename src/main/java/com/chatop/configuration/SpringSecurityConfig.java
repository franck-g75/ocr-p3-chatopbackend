package com.chatop.configuration;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

	Logger log = LoggerFactory.getLogger(SpringSecurityConfig.class);

	@Autowired
    private CustomAuthenticationFilter myFilter;
	
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
        return http
        	.csrf((csrf) -> csrf.ignoringRequestMatchers("/api/auth/login","/api/auth/register")) 	//site with no CSRF protection only on this endpoint
    	   	.addFilterBefore(myFilter,AnonymousAuthenticationFilter.class)   						//CustomAuthenticationFilter for the first connection
    	   	.oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))				//token OAuth2 filter
         	.authorizeHttpRequests(   (authorize) -> authorize										//all the end point must be securised except register
				.requestMatchers(HttpMethod.GET, "/api/auth/me", "/api/user/**", "/api/rentals", "/api/rentals/**" ).authenticated()
				.requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/rentals", "/api/messages").authenticated()
				.requestMatchers(HttpMethod.PUT, "/api/rentals/**").authenticated()
				.requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
				.requestMatchers(HttpMethod.GET, "/image/**", "/swagger-ui/**", "/v3/**").permitAll()    )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   //session stateless
            .exceptionHandling((exceptions) -> exceptions											// popups //found on gitHub https://github.com/spring-projects/spring-security-samples
					.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())			// popups protection
					.accessDeniedHandler(new BearerTokenAccessDeniedHandler())
					)
            .build();
    }
	
	@Bean
	public ModelMapper modelMapper() {
	    return new ModelMapper();
	}
	
}