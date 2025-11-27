package com.chatop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chatop.model.MyDbUser;

/**
 * UserRepository
 */
@Repository
public interface UserRepository extends JpaRepository<MyDbUser,Integer> {

	public MyDbUser findByName(String name);
	public MyDbUser findByEmail(String email);
	public MyDbUser getById(Integer id);
	public MyDbUser save(MyDbUser u);
    
}
