package com.chatop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chatop.model.Message;

/**
 * MessageRepository
 */
@Repository
public interface MessageRepository extends JpaRepository<Message,Integer>{

	public Message save(Message m);
	
}
