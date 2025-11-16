package com.chatop.services;

import java.sql.Timestamp;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chatop.model.Message;
import com.chatop.model.dto.MessageDto;
import com.chatop.repositories.MessageRepository;

@Service
public class MessageService {
	
	Logger log = LoggerFactory.getLogger(MessageService.class);
	
	@Autowired
	UserService userSvc;
	@Autowired
	RentalService rentalSvc;
	@Autowired
	MessageRepository msgRepo;
	
	public Integer save(MessageDto m) {
		
		Message msg = new Message();
		
		msg.setMessage(m.getMessage());
		msg.setRental(rentalSvc.getById(m.getRental_id()));
		msg.setUser(userSvc.getById(m.getUser_id()));
		
		msg.setCreated_at(Timestamp.from(Instant.now()));
		msg.setUpdated_at(Timestamp.from(Instant.now()));
		
		//send an email ??
		
		return msgRepo.save(msg).getId();
	}
	
}
