package com.chatop.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

//import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chatop.model.Rental;
import com.chatop.model.dto.MessageDto;
import com.chatop.model.dto.RentalDto;
import com.chatop.services.MessageService;
import com.chatop.services.RentalService;
import com.chatop.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.IOUtils;

@RestController
public class ResourceController {

	Logger log = LoggerFactory.getLogger(ResourceController.class);
	
	@Value("${server.tomcat.basedir}")
	private String racineServeur;
	
	@Value("${image.basedir}")
	private String racineImage;
	
	@Autowired
	RentalService rentalService;
	
	@Autowired
	UserService dbUserService;
	
	@Autowired
	MessageService msgService;
	
	@GetMapping("/api/rentals/{id}")
	public String getRentalsById( @PathVariable Integer id ) {
		RentalDto myRental = rentalService.getById(id).toDto();
		return myRental.toJson();
	}
		
	@PostMapping(path="/api/rentals", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
	public String AddRental( Authentication authentication,
			@RequestParam("name") String name,
			@RequestParam("surface") Integer surface,
			@RequestParam("price") Integer price,
			@RequestParam("picture") MultipartFile picture,
		    @RequestParam("description") String description) {

		log.trace("authentication.getPrincipal().toString() = " + authentication.getPrincipal().toString() + " et le mail = " + authentication.getName());
		Integer id = rentalService.addRental(name, surface, price, picture, description, authentication.getName());
		
		return "{\"id\":" + id + "}";
	}
	
	
	/**
	 * To get a picture on the page
	 * @param filename
	 * @return a JPEG image
	 * @throws IOException
	 */
	@GetMapping( value = "/image/{filename}", produces = MediaType.IMAGE_JPEG_VALUE	)
	public @ResponseBody ResponseEntity<Resource> getImageWithMediaType( @PathVariable String filename ) throws IOException {
	
		try {
        	log.trace("filename=" + filename);
        	
        	Path rootLocation = Paths.get(racineServeur + "\\" + racineImage + "\\");
            Path file = rootLocation.resolve(filename);
            
            Resource resource = new UrlResource(file.toUri());
            log.trace("resource=" + resource.toString());
            
            if (resource.exists() || resource.isReadable()) {
            	
            	log.trace("resource exist and is readable");
                
            	return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.IMAGE_JPEG) 
                        .body(resource);
            	
            } else {
            	log.trace("resource not found");
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
	}
	
	
	@PutMapping(value= "/api/rentals/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
	public String changeRental( 
			Authentication authentication,
			@PathVariable Integer id,
			@RequestParam("name") String name,
			@RequestParam("surface") Integer surface,
			@RequestParam("price") Integer price,
		    @RequestParam("description") String description) {
		//RentalDto myRental = rentalService;
		Integer idRetour = rentalService.changeRental(id, name, surface, price, description, authentication.getName());
		return "{\"id\":" + idRetour + "}";
	}
	
	@GetMapping("/api/rentals")
	public String getAllRentals() {
		
		String retour ="";
		List<RentalDto> allRentalDto = new ArrayList<RentalDto>();
		
		List<Rental> allRentals = rentalService.findAll();
		if (allRentals != null) {
			for ( Rental r : allRentals ) {
				allRentalDto.add(r.toDto());
			}
		}
		
		//Json conversion
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			retour = objectMapper.writeValueAsString(allRentalDto);
		} catch (JsonProcessingException e) {
			log.error("JsonProcessingException");
		}
		
		return "{\"rentals\":".concat(retour).concat("}");
	}
	
	//---------------------------------MESSAGES--------------------------------------------------------------
	
	@PostMapping("/api/messages")
	public String postMessage(@RequestBody MessageDto requestMsg) {
		String retour ="";
		Integer id = null;
		id = msgService.save(requestMsg);
		retour = "{\"id\":".concat(id.toString()).concat("}");
		return retour;
	}
	
}
