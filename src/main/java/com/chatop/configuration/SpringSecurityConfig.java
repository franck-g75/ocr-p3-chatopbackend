package com.chatop.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    	   	.addFilterBefore(myFilter,AnonymousAuthenticationFilter.class)   //monFiltre anonyme CustomAuthenticationFilter pour la premiere connexion
        	.authorizeHttpRequests((authorize) -> authorize			//toutes les requetes doivent etre authentifiées
				.anyRequest().authenticated()						//soit par httpBasic soit par oauth2ResourceServer
			)
        	.csrf((csrf) -> csrf.ignoringRequestMatchers("/api/auth/login")) //site non protégé sur CSRF uniquement sur cet endpoint
            .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))	// filtre token OAuth2
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   //session sans états
            .exceptionHandling((exceptions) -> exceptions									//protege des popups //trouvé sur gitHub
					.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())	//protege des popups
					.accessDeniedHandler(new BearerTokenAccessDeniedHandler()))				//protege des popups
            .build();
    }
	
}