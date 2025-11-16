package com.chatop.services;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.multipart.MultipartFile;

import com.chatop.exceptions.MyDbException;
import com.chatop.exceptions.MyNotFoundException;
import com.chatop.model.MyDbUser;
import com.chatop.model.Rental;
import com.chatop.repositories.RentalRepository;
import com.chatop.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RentalService {

	Logger log = LoggerFactory.getLogger(RentalService.class);
	
	@Value("${server.tomcat.basedir}")
	private String racineServeur;
	
	@Value("${image.basedir}")
	private String racineImage;
	
	@Value("${image.baseurl}")
	private String urlImage;	
	
	@Autowired
    private RentalRepository rentalRepo;
	@Autowired
    private UserRepository userRepo;
	
	/**
	 * get all rentals 
	 * @return all rentals in database
	 */
	public List<Rental> findAll(){
		List<Rental> retour = new ArrayList<Rental>();
		
		try {
			retour = rentalRepo.findAll();
		} catch (CannotCreateTransactionException ccte) {
			log.error("rentals not found " + ccte.getMessage() + " " + ccte.toString());
			throw new MyDbException("rentals not found " + ccte.getMessage() + " " + ccte.toString());
		} catch (EntityNotFoundException enfe) {
			throw new MyNotFoundException("rentals not found.");
		} catch (Exception e) {
			throw new MyNotFoundException("rentals not found");
		}

		return retour;
	}
	
	/**
	 * get the rental by his id
	 * @param id
	 * @return the rental identified by the id parameter
	 */
	public Rental getById(Integer id) throws MyNotFoundException, MyDbException {
		
		Rental retour = null;
		
		try {
			retour = rentalRepo.getById(id);
		} catch (CannotCreateTransactionException ccte) {
			log.error("rental " + id + " not found " + ccte.getMessage() + " " + ccte.toString());
			throw new MyDbException("rental " + id + " not found " + ccte.getMessage() + " " + ccte.toString());
		} catch (EntityNotFoundException enfe) {
			throw new MyNotFoundException("rental not found id=" + id);
		} catch (Exception e) {
			throw new MyNotFoundException("rental " + id + " not found");
		}
		
		return retour;
	}
	
	
	/**
	 * 
	 * @param name
	 * @param surface
	 * @param price
	 * @param picture
	 * @param description
	 * @param username
	 * @return the id of the new rental
	 */
	public Integer addRental(String name,Integer surface,Integer price,MultipartFile picture,String description,String username) {
		
		Integer retour = 0;
		
		MyDbUser user = null;
		
		try {
			user =  userRepo.findByEmail(username);
		} catch (Exception e) {
			log.error("addRental : user " + username +  " not found ==> " + e.getMessage());
			throw new MyNotFoundException(e.getMessage()) ;
		}
		
		if (user != null) {	
		
			Rental newRental = new Rental();
			newRental.setName(name);
			newRental.setDescription(description);
			newRental.setPrice(price);
			newRental.setSurface(surface);
			newRental.setOwner(user);
			newRental.setUpdated_at( Timestamp.from(Instant.now()) );
			newRental.setCreated_at( Timestamp.from(Instant.now()) );
			
			try {
				newRental = rentalRepo.save(newRental);
			} catch (CannotCreateTransactionException ccte) {
				log.error("rental " + name + " not created " + ccte.getMessage() + " " + ccte.toString());
				throw new MyDbException("rental " + name + " not created " + ccte.getMessage() + " " + ccte.toString());
			} catch (EntityNotFoundException enfe) {
				throw new MyNotFoundException("rental not created name=" + name);
			} catch (Exception e) {
				throw new MyNotFoundException("rental " + name + " not created");
			}
			
			String dir = racineServeur + "\\" + racineImage + "\\";
			String fileName = "rental".concat(newRental.getId().toString().concat(".jpg"));
			
			try {
				Path path = Paths.get(dir + fileName);
			    log.trace("fichier bientot transferé..." + path.toString());
			    picture.transferTo(path.toFile());
			    log.trace("fichier transfere ok");
			} catch (IOException e) {
				log.error("IOException:" + e.toString());
			}
			
			//save the picture name in rental object in db
			newRental.setPicture(urlImage + "/" + fileName);
			rentalRepo.save(newRental);
			
			retour = newRental.getId();
			
		} else {
			log.error("addRental : user not found " + username);
		}
		
		return retour;
	}

	/**
	 * 
	 * @param idRental
	 * @param name
	 * @param surface
	 * @param price
	 * @param description
	 * @param username
	 * @return
	 */
	public Integer changeRental(Integer idRental, String name,Integer surface,Integer price,String description,String username) {
		
		Integer retour = 0;
		
		MyDbUser userConnected = null;			//the user connected
		Rental rentalToChange = null;   //the rental to change
		
		//find user connected
		try {
			userConnected =  userRepo.findByEmail(username);
		} catch (Exception e) {
			log.error("addRental : user " + username +  " not found ==> " + e.getMessage());
			throw new MyNotFoundException("addRental : user " + username +  " not found") ;
		}
		
		//find rental
		try {
			rentalToChange = this.getById(idRental);
		} catch (Exception e) {
			log.error("addRental : rental to change id=" + idRental +  " not found ==> " + e.getMessage());
			throw new MyNotFoundException("addRental : rental to change id=" + idRental +  " not found") ;
		}

		if (userConnected != null) {	
			if (rentalToChange != null) {
				if (userConnected.getId()==rentalToChange.getOwner().getId()) {
					
					//change
					rentalToChange.setName(name);
					rentalToChange.setDescription(description);
					rentalToChange.setPrice(price);
					rentalToChange.setSurface(surface);
					rentalToChange.setUpdated_at( Timestamp.from(Instant.now()) );
					
					//save
					try {
						rentalToChange = rentalRepo.save(rentalToChange);
					} catch (CannotCreateTransactionException ccte) {
						log.error("rental " + name + " not changed " + ccte.getMessage() + " " + ccte.toString());
						throw new MyDbException("rental " + name + " not changed " + ccte.getMessage() + " " + ccte.toString());
					} catch (EntityNotFoundException enfe) {
						throw new MyNotFoundException("rental not changed name=" + name);
					} catch (Exception e) {
						throw new MyNotFoundException("rental " + name + " not changed");
					}
					
					retour = rentalToChange.getId();
				} else {
					log.error("addRental : user connected not == rental user");
				}
			} else {
				log.error("addRental : rental to change not found");
			}
		} else {
			log.error("addRental : user not found " + username);
		}
		
		return retour;
	}
}
