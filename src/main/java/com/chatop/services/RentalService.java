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
import org.springframework.web.multipart.MultipartFile;

import com.chatop.exceptions.MyDbException;
import com.chatop.exceptions.MyNotFoundException;
import com.chatop.model.MyDbUser;
import com.chatop.model.Rental;
import com.chatop.repositories.RentalRepository;
import com.chatop.repositories.UserRepository;

@Service
public class RentalService {

	Logger log = LoggerFactory.getLogger(RentalService.class);
	
	@Value("${server.tomcat.basedir}")
	private String serverBaseDir;
	
	@Value("${image.basedir}")
	private String imageBaseDir;
	
	@Value("${image.baseurl}")
	private String imageUrl;	
	
	@Autowired
    private RentalRepository rentalRepo;
	
	@Autowired
    private UserRepository userRepo;
	
	/**
	 * get all rentals 
	 * @return all rentals in database
	 */
	public List<Rental> findAll() throws MyNotFoundException {
		List<Rental> retour = new ArrayList<Rental>();
		
		try {
			retour = rentalRepo.findAll();
		} catch (Exception e) {
			log.error("rentals not found (other Exception)");
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
		} catch (Exception e) {
			log.error("rental " + id + " not found" + e.toString());
			throw new MyNotFoundException("rental " + id + " not found (other Exception)");
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
	public Integer addRental(Rental paramRental,MultipartFile pic, String username) throws MyNotFoundException, MyDbException, IOException {
		
		Integer retour = 0;
		
		MyDbUser user = null;
		
		try {
			user =  userRepo.findByEmail(username);
		} catch (Exception e) {
			log.error("addRental : user " + username +  " not found ==> " + e.getMessage());
			throw new MyNotFoundException("addRental : find user : " + username +  " not found") ;
		}
		
		Rental newRental = new Rental();
		
		if (user != null) {	
		
			newRental.setDescription(paramRental.getDescription());
			newRental.setName(paramRental.getName());
			newRental.setPrice(paramRental.getPrice());
			newRental.setSurface(paramRental.getSurface());
			newRental.setOwner(user);
			newRental.setUpdated_at( Timestamp.from(Instant.now()) );
			newRental.setCreated_at( Timestamp.from(Instant.now()) );
			
			try {
				newRental = rentalRepo.save(newRental);
			} catch (Exception e) {
				throw new MyNotFoundException("rental " + newRental.getName() + " not created");
			}
			
			log.trace("newRental.getId() = ");
			log.trace(newRental.getId().toString());
			
			String dir = serverBaseDir + "\\" + imageBaseDir + "\\";
			String fileName = "rental".concat(newRental.getId().toString().concat(pic.getOriginalFilename()).concat(".jpg"));
			
			try {
				Path path = Paths.get(dir + fileName);
			    log.trace("fichier bientot transferé..." + path.toString());
			    pic.transferTo(path.toFile());
			    log.trace("fichier transfere : ok");
			} catch (IOException e) {
				log.error("IOException:" + e.toString());
			}
			
			//save the picture name in rental object in db
			newRental.setPicture(imageUrl + "/" + fileName);
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
	 * @return idRental
	 */
	public Integer changeRental(Integer idRental, Rental paramRental, String username) throws MyNotFoundException, MyDbException {
		
		Integer retour = 0;
		
		MyDbUser userConnected = null;	//the user connected
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
					rentalToChange.setName(paramRental.getName());
					rentalToChange.setDescription(paramRental.getDescription());
					rentalToChange.setPrice(paramRental.getPrice());
					rentalToChange.setSurface(paramRental.getSurface());
					rentalToChange.setUpdated_at( Timestamp.from(Instant.now()) );
					
					//save
					try {
						rentalToChange = rentalRepo.save(rentalToChange);
					} catch (Exception e) {
						throw new MyNotFoundException("rental " + paramRental.getName() + " not changed");
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
