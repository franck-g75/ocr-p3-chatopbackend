package com.chatop.services;

import java.sql.Timestamp;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chatop.exceptions.MyWebInfoException;
import com.chatop.model.Message;
import com.chatop.model.MyDbUser;
import com.chatop.model.Rental;
import com.chatop.model.dto.AddMessageDto;
import com.chatop.repositories.MessageRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MessageService {
	
	Logger log = LoggerFactory.getLogger(MessageService.class);
	
	@Autowired
	UserService userSvc;
	
	@Autowired
	RentalService rentalSvc;
	
	@Autowired
	MessageRepository msgRepo;
	
	/**
	 * 
	 * @param m
	 * @return
	 * @throws EntityNotFoundException
	 * @throws MyWebInfoException
	 */
	public Message save(AddMessageDto m) throws EntityNotFoundException , MyWebInfoException {
		
		log.info("saving message...");
		
		Message msg = new Message();
		
		if (m.getMessage().length() > 0 ) {
			msg.setMessage(m.getMessage());
		} else {
			log.error("message is empty : creation not possible.");
			throw new MyWebInfoException("message is empty : creation not possible.");
		}
		
		Rental rental = rentalSvc.getById(m.getRental_id());
		if (rental != null) {
			log.error("Rental not found to create a new message.");
			msg.setRental(rental);
		} else {
			throw new EntityNotFoundException("Rental not found to create a new message.");
		}
		
		MyDbUser user = userSvc.getById(m.getUser_id());
		if (user != null) {
			log.error("User not found to create a new message.");
			msg.setUser(user);
		} else {
			throw new EntityNotFoundException("User not found to create a new message.");
		}
		
		msg.setCreated_at(Timestamp.from(Instant.now()));
		msg.setUpdated_at(Timestamp.from(Instant.now()));
		
		try {
			msg = msgRepo.save(msg);
		} catch (Exception e) {
			log.error("message not created (other Exception)" + e.getMessage() + " " + e.toString());
			throw new EntityNotFoundException("message not created (other Exception)" + e.getMessage() + " " + e.toString());
		}
		
		log.info("message saved.");
		
		return msg;
	}
	
}
