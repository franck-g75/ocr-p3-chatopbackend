package com.chatop.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatop.model.Rental;
import com.chatop.repositories.RentalRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ResourceController {

	Logger log = LoggerFactory.getLogger(ResourceController.class);
	
	@Autowired
	RentalRepository rentalRepo;
	
	@GetMapping("/api/rentals")
	public String getRentals() {
		
		String retour ="";
		
		Rental[] allRentals = rentalRepo.findAll().toArray(new Rental[0]);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			retour = objectMapper.writeValueAsString(allRentals);
		} catch (JsonProcessingException e) {
			log.error("JsonProcessingException");
		}
		return "{\"rentals\":".concat(retour).concat("}");
		
		/*
		return "{ \r\n"
				+ "  \"rentals\": [\r\n"
				+ "  {\r\n"
				+ "	\"id\": 1,\r\n"
				+ "	\"name\": \"test house 1\",\r\n"
				+ "	\"surface\": 432,\r\n"
				+ "	\"price\": 300,\r\n"
				+ "	\"picture\": \"https://blog.technavio.org/wp-content/uploads/2018/12/Online-House-Rental-Sites.jpg\",\r\n"
				+ "	\"description\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam a lectus eleifend, varius massa ac, mollis tortor. Quisque ipsum nulla, faucibus ac metus a, eleifend efficitur augue. Integer vel pulvinar ipsum. Praesent mollis neque sed sagittis ultricies. Suspendisse congue ligula at justo molestie, eget cursus nulla tincidunt. Pellentesque elementum rhoncus arcu, viverra gravida turpis mattis in. Maecenas tempor elementum lorem vel ultricies. Nam tempus laoreet eros, et viverra libero tincidunt a. Nunc vel nisi vulputate, sodales massa eu, varius erat.\",\r\n"
				+ "	\"owner_id\": 1,\r\n"
				+ "	\"created_at\": \"2012/12/02\",\r\n"
				+ "	\"updated_at\": \"2014/12/02\"  \r\n"
				+ "},\r\n"
				+ "{\r\n"
				+ "	\"id\": 1,\r\n"
				+ "	\"name\": \"test house 2\",\r\n"
				+ "	\"surface\": 154,\r\n"
				+ "	\"price\": 200,\r\n"
				+ "	\"picture\": \"https://blog.technavio.org/wp-content/uploads/2018/12/Online-House-Rental-Sites.jpg\",\r\n"
				+ "	\"description\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam a lectus eleifend, varius massa ac, mollis tortor. Quisque ipsum nulla, faucibus ac metus a, eleifend efficitur augue. Integer vel pulvinar ipsum. Praesent mollis neque sed sagittis ultricies. Suspendisse congue ligula at justo molestie, eget cursus nulla tincidunt. Pellentesque elementum rhoncus arcu, viverra gravida turpis mattis in. Maecenas tempor elementum lorem vel ultricies. Nam tempus laoreet eros, et viverra libero tincidunt a. Nunc vel nisi vulputate, sodales massa eu, varius erat.\",\r\n"
				+ "	\"owner_id\": 2,\r\n"
				+ "	\"created_at\": \"2012/12/02\",\r\n"
				+ "	\"updated_at\": \"2014/12/02\"  \r\n"
				+ "},{\r\n"
				+ "	\"id\": 3,\r\n"
				+ "	\"name\": \"test house 3\",\r\n"
				+ "	\"surface\": 234,\r\n"
				+ "	\"price\": 100,\r\n"
				+ "	\"picture\": \"https://blog.technavio.org/wp-content/uploads/2018/12/Online-House-Rental-Sites.jpg\",\r\n"
				+ "	\"description\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam a lectus eleifend, varius massa ac, mollis tortor. Quisque ipsum nulla, faucibus ac metus a, eleifend efficitur augue. Integer vel pulvinar ipsum. Praesent mollis neque sed sagittis ultricies. Suspendisse congue ligula at justo molestie, eget cursus nulla tincidunt. Pellentesque elementum rhoncus arcu, viverra gravida turpis mattis in. Maecenas tempor elementum lorem vel ultricies. Nam tempus laoreet eros, et viverra libero tincidunt a. Nunc vel nisi vulputate, sodales massa eu, varius erat.\",\r\n"
				+ "	\"owner_id\": 1,\r\n"
				+ "	\"created_at\": \"2012/12/02\",\r\n"
				+ "	\"updated_at\": \"2014/12/02\"  \r\n"
				+ "}\r\n"
				+ "  \r\n"
				+ "  ]\r\n"
				+ "}";
				*/
	}
}
