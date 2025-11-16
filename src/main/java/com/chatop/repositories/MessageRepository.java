package com.chatop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chatop.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message,Integer>{
	/*
	public List<Message> findAll();
	public List<Message> findByRental(Rental r);
	public List<Message> findByUser(MyDbUser u);
	public List<Message> findByRentalAndByUser(Rental r, MyDbUser u); 
	*/
	public Message save(Message m);
	
}
