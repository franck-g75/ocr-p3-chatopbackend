package com.chatop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chatop.model.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental,Integer> {
	
	public List<Rental> findAll();
	public Rental getById(Integer id);
	
}
