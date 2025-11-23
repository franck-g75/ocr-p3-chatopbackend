package com.chatop.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.chatop.exceptions.MyDbException;
import com.chatop.exceptions.MyNotFoundException;
import com.chatop.exceptions.MyWebInfoException;
import com.chatop.model.Message;
import com.chatop.model.MyDbUser;
import com.chatop.model.Rental;
import com.chatop.model.dto.AddRentalDto;
import com.chatop.model.dto.ChangeRentalDto;
import com.chatop.model.dto.MessageDto;
import com.chatop.model.dto.ReadRentalDto;
import com.chatop.services.MessageService;
import com.chatop.services.RentalService;
import com.chatop.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@Tag(name = "ResourceController", description = "rental, messages and image management API")
@RestController
public class ResourceController {

	Logger log = LoggerFactory.getLogger(ResourceController.class);
	
	@Value("${server.tomcat.basedir}")
	private String serverBaseDir;
	
	@Value("${image.basedir}")
	private String imageBaseDir;
	
	@Autowired
	RentalService rentalService;
	
	@Autowired
	UserService dbUserService;
	
	@Autowired
	MessageService msgService;
	
	@Autowired
	ModelMapper modelMapper;
	
	
	
	
	
	@Operation(summary="get rental by id.",
			parameters = {
					@Parameter(name = "id", description = "the id of the rental.", required = true)
				},
				responses = {
				    @ApiResponse(responseCode = "200", description = "rental returned."),
				    @ApiResponse(responseCode = "400", description = "rental not found (Bad request)"),
				    @ApiResponse(responseCode = "401", description = "User not connected (unauthorised)"),
				    @ApiResponse(responseCode = "500", description = "Server error")
				})
	@GetMapping("/api/rentals/{id}")
	public String getRentalsById( @PathVariable Integer id ) throws MyNotFoundException, MyDbException, IOException {
		ReadRentalDto myRental = convertToReadDto(rentalService.getById(id));
		String retour = myRental.toJson();
		return retour;
	}
	
	
	
	
	
	
	
	@Operation(summary="new rental.",
			parameters = {
					@Parameter(name = "name", description = "the user name.", required = true),
					@Parameter(name = "surface", description = "the surface of the rental.", required = true),
					@Parameter(name = "price", description = "the priceof the rental.", required = true),
					@Parameter(name = "picture", description = "the picture to upload.", required = true),
					@Parameter(name = "description", description = "the description of the rental.", required = true)
				},
				responses = {
				    @ApiResponse(responseCode = "201", description = "rental created."),
				    @ApiResponse(responseCode = "400", description = "Bad request"),
				    @ApiResponse(responseCode = "401", description = "User not connected (unauthorised)"),
				    @ApiResponse(responseCode = "413", description = "Payload too large"),
				    @ApiResponse(responseCode = "500", description = "Server error")
				})
	@PostMapping(path="/api/rentals", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
	public String addRental( Authentication authentication,  @ModelAttribute AddRentalDto addRentalDto ) 
					throws MyNotFoundException, MyDbException, IOException {
		
		log.trace("authentication.getPrincipal().toString() = " + authentication.getPrincipal().toString() + " et le mail = " + authentication.getName());
		
		Rental outRental = convertToEntity(addRentalDto);
		
		//throws exceptions
		rentalService.addRental(outRental, addRentalDto.getPicture(), authentication.getName());
		
		return "{\"message\":\"rental created\"}";
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Operation(summary="new rental.",
			parameters = {
					@Parameter(name = "name", description = "the rental name.", required = true),
					@Parameter(name = "surface", description = "the surface of the rental.", required = true),
					@Parameter(name = "price", description = "the price of the rental.", required = true),
					@Parameter(name = "description", description = "the description of the rental.", required = true)
				},
				responses = {
				    @ApiResponse(responseCode = "201", description = "rental created."),
				    @ApiResponse(responseCode = "400", description = "Bad request"),
				    @ApiResponse(responseCode = "401", description = "User not connected (unauthorised)"),
				    @ApiResponse(responseCode = "404", description = "rental not found"),
				    @ApiResponse(responseCode = "500", description = "Server error")
				})
	@PutMapping( value= "/api/rentals/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
	public String changeRental( 
					Authentication authentication,
					@PathVariable Integer id,
					@ModelAttribute ChangeRentalDto changeRentalDto)
					throws MyNotFoundException, MyDbException {
		
			log.trace("authentication.getPrincipal().toString() = " + authentication.getPrincipal().toString() + " et le mail = " + authentication.getName());

			Rental outRental = convertToEntity(changeRentalDto,id);
		
			rentalService.changeRental(id, outRental, authentication.getName());
		
		return "{\"message\":\"Rental updated !\"}";
	}
	
	
	
	
	
	


	@Operation(summary="get all rentals.",
			responses = {
				    @ApiResponse(responseCode = "200", description = "rentals returned."),
				    @ApiResponse(responseCode = "401", description = "User not connected (unauthorised)"),
				    @ApiResponse(responseCode = "404", description = "rentals not found"),
				    @ApiResponse(responseCode = "500", description = "Server error")
				})
	@GetMapping("/api/rentals")
	public String getAllRentals() throws MyNotFoundException, MyDbException, IOException {
		
		String retour ="";
		
		List<Rental> allRentals = rentalService.findAll();
		
		if (allRentals != null) {
			retour = "{\"rentals\": [";
			for ( Rental rental : allRentals ) {
				ReadRentalDto readRentaldto = convertToReadDto(rental);
				retour = retour + readRentaldto.toJson() + ",";
			}
			retour = retour.substring(0, retour.length()-1).concat("]}");
		} else {
			log.error("EntityNotFoundException : rentals not found");
			throw new EntityNotFoundException("EntityNotFoundException : rentals not found");
		}
		
		return retour;
	}
	
	
	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * To get a picture on the page
	 * @param filename
	 * @return a JPEG image
	 * @throws IOException
	 */
	@Operation(summary="get a rental picture. (only jpeg < 8000kb)",
			parameters = {
					@Parameter(name = "filename", description = "the server filename of the image", required = true)
				},
				responses = {
				    @ApiResponse(responseCode = "200", description = "image found and retrurned."),
				    @ApiResponse(responseCode = "400", description = "Bad request"),
				    @ApiResponse(responseCode = "500", description = "Server error")
				})
	@GetMapping( value = "/image/{filename}", produces = MediaType.IMAGE_JPEG_VALUE	)
	public @ResponseBody ResponseEntity<Resource> getImageWithMediaType( @PathVariable String filename ) 
			throws IOException, MyNotFoundException, NoResourceFoundException, MalformedURLException {
	
		try {
        	log.trace("filename=" + filename);
        	
        	Path rootLocation = Paths.get(serverBaseDir + "\\" + imageBaseDir + "\\");
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
            	log.trace("resource not found or not readable");
                throw new NoResourceFoundException(HttpMethod.GET,"resource not found or not readable");
            }
        } catch (MalformedURLException e) {
        	log.trace("Error MalformedURLException for filename : " + e.getMessage());
            throw new MalformedURLException("Error MalformedURLException for filename : " + e.getMessage());
        }
	}
	
	
	//---------------------------------MESSAGES--------------------------------------------------------------
	
	@Operation(summary="new message form.",
			parameters = {
					@Parameter(name = "message", description = "the message contains the text of the message.", required = true),
					@Parameter(name = "user_id", description = "the user identifier.", required = true),
					@Parameter(name = "rental_id", description = "the rental identifier.", required = true)
				},
				responses = {
				    @ApiResponse(responseCode = "201", description = "Message created"),
				    @ApiResponse(responseCode = "400", description = "Message not created (bad request)"),
				    @ApiResponse(responseCode = "401", description = "User not connected (unauthorised)"),
				    @ApiResponse(responseCode = "404", description = "Not found : The user or the rental is not found."),
				    @ApiResponse(responseCode = "500", description = "Server error")
				})
	@PostMapping("/api/messages")
	public ResponseEntity<String> postMessage(@RequestBody MessageDto requestMsg) throws MyNotFoundException, MyWebInfoException, URISyntaxException {
		log.info("postMessage... requestMsg = " + requestMsg.toJson() );
		Message msg = msgService.save(requestMsg);
		log.info("msgDto saved = {\"message\":\"".concat(msg.getId().toString()).concat("\"}"));
        URI returnedUri = new URI( "http://localhost:8080/api/messages/".concat(msg.getId().toString()) );
        log.info("returnedUri =" + returnedUri);
        return ResponseEntity.created( returnedUri ).body("{\"message\": \"Message send with success\"}");
	}
	
//-----------------------------------Private------------------------------------------------------------------------------------------------------------	
	
	private ReadRentalDto convertToReadDto(Rental rental) {
		ReadRentalDto readRentalDto = modelMapper.map(rental, ReadRentalDto.class);
		readRentalDto.setOwner_id(rental.getOwner().getId());
	    return readRentalDto;
	}
	
	
	private Rental convertToEntity(ChangeRentalDto changeRentalDto, Integer id) throws ParseException {
		
	    Rental rental = modelMapper.map(changeRentalDto, Rental.class);
	    
	    if (id != null) {
	    	
	    	Rental oldRental = null;
	    	MyDbUser theOwner = null;
	    	
	    	try {
	    		oldRental = rentalService.getById(id);
	    	} catch(EntityNotFoundException e) {
	    		log.error("EntityNotFoundException : rental with id = " + id + " not found.");
	    		throw new EntityNotFoundException("EntityNotFoundException : rental with id = " + id + " not found.");
	    	}
	    	
	    	try {
	    		theOwner = dbUserService.getById(oldRental.getOwner().getId());
	    	} catch(EntityNotFoundException e) {
	    		log.error("EntityNotFoundException : Owner user with id = " + oldRental.getOwner().getId() + " not found.");
	    		throw new EntityNotFoundException("EntityNotFoundException : Owner user with id = " + oldRental.getOwner().getId() + " not found.");
	    	}
	    	
	    	rental.setPicture(oldRental.getPicture());
	    	rental.setCreated_at(oldRental.getCreated_at());
	        rental.setOwner(theOwner);

	    }
	    
	    return rental;
	}
	
	private Rental convertToEntity(AddRentalDto addRentalDto) throws ParseException {
		
		return modelMapper.map(addRentalDto, Rental.class);
		
	}
}
