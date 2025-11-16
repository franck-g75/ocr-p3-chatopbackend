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

}
