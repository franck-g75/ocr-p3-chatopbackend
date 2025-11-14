package com.chatop.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatop.model.dto.RentalDto;
import com.chatop.services.RentalService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ResourceController {

	Logger log = LoggerFactory.getLogger(ResourceController.class);
	
	@Autowired
	RentalService rentalService;
	
	@GetMapping("/api/rentals/{id}")
	public String getRentalsById( @PathVariable Integer id ) {
		RentalDto myRental = rentalService.getById(id);
		return myRental.toJson();
	}
	
	@PostMapping("/api/rentals/")
	public String AddtRental(  ) {
		//RentalDto myRental = rentalService;
		return "";
	}
	
	@GetMapping("/api/rentals")
	public String getRentals() {
		
		String retour ="";
		
		RentalDto[] allRentals = rentalService.findAll().toArray(new RentalDto[0]);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			retour = objectMapper.writeValueAsString(allRentals);
		} catch (JsonProcessingException e) {
			log.error("JsonProcessingException");
		}
		return "{\"rentals\":".concat(retour).concat("}");
	}
	
	
	
}
