package com.chatop.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.chatop.exceptions.MyConflictException;
import com.chatop.exceptions.MyDbException;
import com.chatop.exceptions.MyNotFoundException;
import com.chatop.exceptions.MyWebInfoException;
import com.chatop.model.MyDbUser;
import com.chatop.model.dto.LoginDto;
import com.chatop.model.dto.ReadUserDto;
import com.chatop.model.dto.AddUserDto;
import com.chatop.repositories.UserRepository;
import com.chatop.services.JWTService;
import com.chatop.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "UserController", description = "user management API")
@SecurityScheme(name="bearerAuth", type=SecuritySchemeType.HTTP, scheme="bearer", bearerFormat="JWT" )
@RestController
public class UserController {
	
	Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
    private JWTService jwtService;
	
	@Autowired 
	UserService userService;
	
	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired 
	BCryptPasswordEncoder passwordEncoder;
	

	
	
	
	
	
    /**
     * authentication mapping - used to create a token
     * @param authentication
     * @return the token
     */
	@Operation(	summary="login form. use this end point to create a token", 
				responses = {
				    @ApiResponse(responseCode = "200", description = "User connected"),
				    @ApiResponse(responseCode = "401", description = "User not connected (unauthorised)"),
				    @ApiResponse(responseCode = "403", description = "User not found in DB (forbiden)"),
				    @ApiResponse(responseCode = "500", description = "Server error")
				}     )
    @PostMapping("/api/auth/login")
    public ResponseEntity<String> postLogin( @RequestBody LoginDto loginDto) throws NoResourceFoundException {  
		String token="";
		MyDbUser user_db = null;								//the DB user found (or not) in the dataBase
		UsernamePasswordAuthenticationToken userToken = null;  //the authenticated user found
		
		//find the user by his email in the database
		try {
			user_db = this.userRepo.findByEmail(loginDto.getEmail()); //same email
		} catch (DataAccessResourceFailureException ex) {
			throw new DataAccessResourceFailureException("DB connection not avaiable...");
		}
    	
    	if (Objects.isNull(user_db)) {
    		log.error("User not found in DB");
    		throw new NoResourceFoundException(HttpMethod.POST,"User not found in DB.");
    	} else {
    		if ( passwordEncoder.matches( loginDto.getPassword() , user_db.getPassword() ) ) {
    			//passwords and emails are matching.
    			final List<SimpleGrantedAuthority> grantedAuths = new ArrayList<>(); //empty role list
    			final String encodedPassword = this.passwordEncoder.encode( loginDto.getPassword() ); // password is encoded
    	        final UserDetails principal = new User( loginDto.getEmail() , encodedPassword , grantedAuths ); // 
    	        userToken = new UsernamePasswordAuthenticationToken( principal , encodedPassword , grantedAuths );
    			SecurityContextHolder.getContext().setAuthentication(userToken); //authenticate the user 
    			token = jwtService.generateToken(userToken);
			} else {
				log.error("Authentication failed");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"error\"}");
			}
    	}
    	//log.info("api-auth-login path return token");
	    return ResponseEntity.ok().body("{\"token\":\"".concat(token).concat("\"}")); 
    }
	
	
	
    /**
     * get the user connected informations
     * @param authentication
     * @return
     */
	@Operation(summary="connected user informations.", security=@SecurityRequirement(name="bearerAuth"),
			responses = {
			    @ApiResponse(responseCode = "200", description = "User connected : informations are given"),
			    @ApiResponse(responseCode = "401", description = "User not connected (unauthorised)"),
			    @ApiResponse(responseCode = "500", description = "Server error")
			})
    @GetMapping("/api/auth/me")
    public ResponseEntity<String> getUserConnectedInfo(Authentication authentication) throws MyNotFoundException, IOException {
    	return ResponseEntity.ok().body(  convertToReadDto(userService.findByEmail( authentication.getName() )).toJson()   );
    }
    
	
	
    /**
     * get user by id
     * @return the user informations identified by the user id in the url
     */
	@Operation(summary="user (identified by id parameter) informations ",  security=@SecurityRequirement(name="bearerAuth"),
			parameters = {
				@Parameter(name = "id", description = "user id", required = true)
			},
			responses = {
				@ApiResponse(responseCode = "200", description = "User connected : informations are given"),
				@ApiResponse(responseCode = "400", description = "User not found (bad_request)"),
				@ApiResponse(responseCode = "401", description = "User not connected (unauthorised)"),
				@ApiResponse(responseCode = "404", description = "User not found"),
				@ApiResponse(responseCode = "500", description = "Server error (DB unavaiable or other ...)")
			})
    @GetMapping("/api/user/{id}")
    public ResponseEntity<String> getUserById( @PathVariable Integer id ) throws CannotCreateTransactionException, MyNotFoundException, IOException {
		return ResponseEntity.ok().body( convertToReadDto(userService.getById(id)).toJson() );
    }
    
	
	
    /**
     * New user
     * @param authentication
     * @return registered if OK or empty if not
     * @throws URISyntaxException 
     */
	@Operation(summary="new user form.",
				responses = {
				    @ApiResponse(responseCode = "201", description = "User created"),
				    @ApiResponse(responseCode = "400", description = "User not created (bad request)"),
				    @ApiResponse(responseCode = "409", description = "User not created (conflict)"),
				    @ApiResponse(responseCode = "500", description = "Server error")
				})
    @PostMapping("/api/auth/register")
    public ResponseEntity<String> postNewUser(@RequestBody AddUserDto requestBody) 
    				throws DataAccessResourceFailureException, MyWebInfoException, MyDbException, URISyntaxException, MyConflictException,IOException {
    	log.info("CustomRequestBody = " + requestBody.toString() + ")");
    	
    	if ( requestBody.getPassword().length() < 3 ) { //check password length...
			log.error("UserController : Password must be at least 3 char");
			throw new MyWebInfoException("UserController : password must be at least 3 char");
		}
    	
    	if ( requestBody.getName().length() < 3 ) { //check name length...
			log.error("UserController : name must be at least 3 char");
			throw new MyWebInfoException("UserController : name must be at least 3 char");
		}
    	
    	if ( userService.findByEmail(requestBody.getEmail()) != null ){
    		log.error("The user already exist...");
    		throw new MyConflictException("The user already exist...");
    	}
    	
    	//password hashing
    	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());
    	String shaSalted = bCryptPasswordEncoder.encode( requestBody.getPassword() );
    	
    	//password saving
    	MyDbUser user = userService.saveUser( requestBody.getEmail(), requestBody.getName(), shaSalted );
   		log.info("myDbUser just created = " + convertToReadDto( user ).toJson());
   		
		//user is created, if not : an exception has been thrown; now authentication of the user :
    	final List<SimpleGrantedAuthority> grantedAuths = new ArrayList<>(); //empty list
        final UserDetails principal = new User(requestBody.getEmail(), shaSalted, grantedAuths);
        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(principal, shaSalted, grantedAuths);
        SecurityContextHolder.getContext().setAuthentication(userToken); //authenticate the user just created
        
        //token creation
        String token = jwtService.generateToken(userToken);
        
        //build the returned URI
        URI returnedUri = new URI( "http://localhost:8080/api/user/".concat(user.getId().toString()) );
        
        //return the token
        return ResponseEntity.created( returnedUri ).body("{\"token\":\"".concat(token).concat("\"}"));
    }
	

	//----------------------------------------------------------------------------------------------------------------------
	
	
	private ReadUserDto convertToReadDto(MyDbUser dbUser) {
		return modelMapper.map(dbUser, ReadUserDto.class);
	}
	
	
}
