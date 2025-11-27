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
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.HeadersBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.CannotCreateTransactionException;
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
import com.chatop.model.dto.AddMessageDto;
import com.chatop.model.dto.AddRentalDto;
import com.chatop.model.dto.ChangeRentalDto;
import com.chatop.model.dto.ReadRentalDto;
import com.chatop.services.MessageService;
import com.chatop.services.RentalService;
import com.chatop.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@Tag(name = "ResourceController", description = "rental, messages and image management API")
@SecurityScheme(name="bearerAuth", type=SecuritySchemeType.HTTP, scheme="bearer", bearerFormat="JWT" )
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
	
	
	
	
	
	
	
	
	/**
	 * getAllRentals
	 * @return
	 * @throws MyNotFoundException
	 * @throws MyDbException
	 * @throws IOException
	 */
	@Operation(summary="get all rentals.", security=@SecurityRequirement(name="bearerAuth"),
			responses = {
				    @ApiResponse(responseCode = "200", description = "rentals returned."),
				    @ApiResponse(responseCode = "401", description = "User not connected (unauthorized)"),
				    @ApiResponse(responseCode = "404", description = "rentals not found"),
				    @ApiResponse(responseCode = "500", description = "Server error")
				})
	@GetMapping("/api/rentals")
	public ResponseEntity<String> getAllRentals() throws MyNotFoundException, IOException {
		
		String rentalTab="";
		
		try {
			
			List<Rental> allRentals = rentalService.findAll();
			
			if (allRentals != null) {
				rentalTab = "{\"rentals\": [";
				for ( Rental rental : allRentals ) {
					ReadRentalDto readRentaldto = convertToReadDto(rental);
					rentalTab = rentalTab + readRentaldto.toJson() + ",";
				}
				rentalTab = rentalTab.substring(0, rentalTab.length()-1).concat("]}");
			} else {
				log.error("EntityNotFound : all rental is null");
				return ResponseEntity.badRequest().body("EntityNotFound all rental is null ");
			}
		} catch (CannotCreateTransactionException ex) {
			return ResponseEntity.internalServerError().body("{\"message\":\"DB connection not avaiable...\"}" );//500
		} catch(MyNotFoundException mnfe) {
			return ResponseEntity.notFound().build();//404
		} catch (IOException ioe) {
			return ResponseEntity.internalServerError().body("{\"message\":\"IOException " + ioe.getMessage()+"\"}" );//500
		} 
		
		return ResponseEntity.ok(rentalTab);//200
	}
	
	
	
	
	
	
	/**
	 * getRentalsById
	 * @param id
	 * @return 
	 * @throws MyNotFoundException
	 * @throws MyDbException
	 * @throws IOException
	 */
	@Operation(summary="get rental by id.", security=@SecurityRequirement(name="bearerAuth"),
			parameters = {
					@Parameter(name="id", description="the id of the rental.", required=true)
				},
			responses = {
			    @ApiResponse(responseCode = "200", description = "rental returned."),
			    @ApiResponse(responseCode = "400", description = "rental not found (Bad request)"),
			    @ApiResponse(responseCode = "401", description = "User not connected (unauthorised)"),
			    @ApiResponse(responseCode = "404", description = "rental not found (not found)"),
			    @ApiResponse(responseCode = "500", description = "Server error")
			})
	@GetMapping("/api/rentals/{id}")
	public ResponseEntity<String> getRentalsById( @PathVariable Integer id ) {
		String rentalDtoToJson="";
		
		try {
			rentalDtoToJson = convertToReadDto(rentalService.getById(id)).toJson();
		} catch (CannotCreateTransactionException e) {
			return ResponseEntity.internalServerError().body("{\"message\":\"" + e.getMessage() +"\"}" );
		} catch (MyNotFoundException mnfe) {
			return ResponseEntity.badRequest().body("{\"message\":\"MyNotFoundException" + mnfe.getMessage() +"\"}" );
		} catch (IOException ioe) {
			return ResponseEntity.internalServerError().body("{\"message\":\"IOException" + ioe.getMessage() + "\"}" );
		}
	
		return ResponseEntity.ok( rentalDtoToJson );
	}
	
	
	
	
	
	
	/**
	 * addRental
	 * @param authentication
	 * @param addRentalDto
	 * @return
	 * @throws MyNotFoundException
	 * @throws MyDbException
	 * @throws IOException
	 */
	@Operation(summary="new rental.", security=@SecurityRequirement(name="bearerAuth"),
				responses = {
				    @ApiResponse(responseCode = "201", description = "rental created."),
				    @ApiResponse(responseCode = "400", description = "Bad request"),
				    @ApiResponse(responseCode = "401", description = "User not connected (unauthorised)"),
				    @ApiResponse(responseCode = "413", description = "Payload too large"),
				    @ApiResponse(responseCode = "500", description = "Server error")
				})
	@PostMapping(path="/api/rentals", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
	public ResponseEntity<String> addRental( Authentication authentication,  @ModelAttribute AddRentalDto addRentalDto ) 
					throws MyNotFoundException, MyDbException, IOException {
		Integer rentalId = 0;
		URI returnedUri = null;
		
		log.trace("authentication.getPrincipal().toString() = " + authentication.getPrincipal().toString() + " et le mail = " + authentication.getName());
		try {
			
			Rental outRental = convertToEntity(addRentalDto);
			rentalId = rentalService.addRental(outRental, addRentalDto.getPicture(), authentication.getName());//throws exceptions
			returnedUri =  new URI( "http://localhost:8080/api/rentals/" + rentalId.toString() );
			
		} catch (URISyntaxException e) {
			return ResponseEntity.internalServerError().body("URISyntaxException" + e.getMessage());
		} catch (MyNotFoundException mnfe) {
			return ResponseEntity.badRequest().body("MyNotFoundException" + mnfe.getMessage());
		} catch (MyDbException mdbe) {
			return ResponseEntity.internalServerError().body("MyDbException" + mdbe.getMessage());
		} catch (IOException ioe) {
			return ResponseEntity.internalServerError().body("IOException" + ioe.getMessage());
		}
		
		return ResponseEntity.created(returnedUri).body("{\"message\":\"rental created\"}");
		
	}
	
	

	
	/**
	 * changeRental
	 * @param authentication
	 * @param id
	 * @param changeRentalDto
	 * @return
	 * @throws MyNotFoundException
	 * @throws MyDbException
	 */
	@Operation(summary="change rental.", security=@SecurityRequirement(name="bearerAuth"),
				responses = {
				    @ApiResponse(responseCode = "201", description = "rental created."),
				    @ApiResponse(responseCode = "400", description = "Bad request"),
				    @ApiResponse(responseCode = "401", description = "User not connected (unauthorised)"),
				    @ApiResponse(responseCode = "404", description = "rental not found"),
				    @ApiResponse(responseCode = "500", description = "Server error")
				})
	@PutMapping( value= "/api/rentals/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
	public ResponseEntity<String> changeRental( 
					Authentication authentication,
					@PathVariable Integer id,
					@ModelAttribute ChangeRentalDto changeRentalDto)
					throws MyNotFoundException, MyDbException {
		
		log.trace("authentication.getPrincipal().toString() = " + authentication.getPrincipal().toString() + " et le mail = " + authentication.getName());
			
		try {
			
			Rental outRental = convertToEntity(changeRentalDto,id);
			rentalService.changeRental(id, outRental, authentication.getName());
			
		} catch(MyNotFoundException mnfe) {
			return ResponseEntity.badRequest().body("MyNotFoundException " + mnfe.getMessage());
		} catch(MyDbException mdbe) {
			return ResponseEntity.internalServerError().body("MyDbException " + mdbe.getMessage());
		} catch(ParseException pe) {
			return ResponseEntity.internalServerError().body("ParseException " + pe.getMessage());
		} 
		
		return ResponseEntity.ok().body("{\"message\":\"Rental updated !\"}");
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
	
	@Operation(summary="new message form.", security=@SecurityRequirement(name="bearerAuth"),
			parameters = {
					@Parameter(name = "message", description = "the message contains the text of the message.", required = true),
					@Parameter(name = "user_id", description = "the user identifier.", required = true),
					@Parameter(name = "rental_id", description = "the rental identifier.", required = true)
				},
				responses = {
				    @ApiResponse(responseCode = "201", description = "Message created"),
				    @ApiResponse(responseCode = "400", description = "Message not created (bad request)"),
				    @ApiResponse(responseCode = "401", description = "User not connected (unauthorised)"),
				    @ApiResponse(responseCode = "403", description = "invalid CSRF token (forbiden)"),
				    @ApiResponse(responseCode = "404", description = "Not found : The user or the rental is not found."),
				    @ApiResponse(responseCode = "500", description = "Server error")
				})
	@PostMapping("/api/messages")
	public ResponseEntity<String> postMessage(@RequestBody AddMessageDto requestMsg) throws MyNotFoundException, MyWebInfoException, URISyntaxException {
		log.info("postMessage... requestMsg = " + requestMsg.toJson() );
		Message msg = msgService.save(requestMsg);
		log.info("msgDto saved = {\"message\":\"".concat(msg.getId().toString()).concat("\"}"));
        URI returnedUri = new URI( "http://localhost:8080/api/messages/".concat(msg.getId().toString()) );
        log.info("returnedUri =" + returnedUri);
        return ResponseEntity.created( returnedUri ).body("{\"message\": \"Message send with success\"}");
	}
	
//-----------------------------------Private------------------------------------------------------------------------------------------------------------	
	
	/**
	 * Convert a rental to a ReadRentalDto
	 * @param rental
	 * @return
	 */
	private ReadRentalDto convertToReadDto(Rental rental) {
		ReadRentalDto readRentalDto = modelMapper.map(rental, ReadRentalDto.class);
		readRentalDto.setOwner_id(rental.getOwner().getId());
	    return readRentalDto;
	}
	
	/**
	 * Convert a changeRentalDto to a Rental
	 * @param changeRentalDto
	 * @param id
	 * @return
	 * @throws ParseException
	 */
	private Rental convertToEntity(ChangeRentalDto changeRentalDto, Integer id) throws ParseException, EntityNotFoundException {
		
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
	
	
	/**
	 * Convert a addREntalDto to a Rental
	 * @param addRentalDto
	 * @return
	 * @throws ParseException
	 */
	private Rental convertToEntity(AddRentalDto addRentalDto) throws ParseException {
		return modelMapper.map(addRentalDto, Rental.class);
	}
}
