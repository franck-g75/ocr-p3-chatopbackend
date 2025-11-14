package com.chatop.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chatop.exceptions.MyNotFoundException;
import com.chatop.model.Rental;
import com.chatop.model.dto.RentalDto;
import com.chatop.repositories.RentalRepository;

@Service
public class RentalService {

	@Autowired
    private RentalRepository rentalRepo;
	
	/**
	 * get all rentals 
	 * @return all rentals in database
	 */
	public List<RentalDto> findAll(){
		List<RentalDto> retour = new ArrayList<RentalDto>();
		List<Rental> rentals = rentalRepo.findAll();
		if (rentals != null) {
			for (Rental r : rentals  ) {
				retour.add(r.toDto());
			}
		}
		return retour;
	}
	
	/**
	 * get the rental by his id
	 * @param id
	 * @return the rental identified by the id parameter
	 */
	public RentalDto getById(Integer id) throws MyNotFoundException {
		RentalDto retour = new RentalDto();
		Rental myRental = rentalRepo.getById(id);
		if (myRental != null) {
			retour = myRental.toDto();
		} else {
			throw new MyNotFoundException("Rental not found");
		}
		return retour;
	}
}
