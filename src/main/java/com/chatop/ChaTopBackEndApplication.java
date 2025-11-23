package com.chatop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition( info = @Info(title = "rentals management API", version = "1.0") )
public class ChaTopBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChaTopBackEndApplication.class, args);
	}

}
