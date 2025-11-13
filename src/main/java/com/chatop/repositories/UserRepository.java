package com.chatop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chatop.model.MyDbUser;

@Repository
public interface UserRepository extends JpaRepository<MyDbUser,Integer> {

	MyDbUser findByName(String name);
	MyDbUser findByEmail(String email);
	MyDbUser getById(Integer id);
	MyDbUser save(MyDbUser u);
    
}
