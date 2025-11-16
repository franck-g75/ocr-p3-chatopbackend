package com.chatop.model.dto;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;

import com.chatop.model.Message;
import com.chatop.model.MyDbUser;
import com.chatop.model.Rental;

public class MessageDto {

	private Integer id;
	private Integer rental_id;
	private Integer user_id;
	private String message;
	private Timestamp created_at;
	private Timestamp updated_at;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	public Integer getRental_id() {
		return rental_id;
	}
	public void setRental_id(Integer rental_id) {
		this.rental_id = rental_id;
	}
	
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	
	
	public Timestamp getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}

	
	
	public Timestamp getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Timestamp updated_at) {
		this.updated_at = updated_at;
	}
	
	
	//--------------------------------------------------------------------------
	
	public String toJson() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		return "{\r\n"
		+ " \"id\":" + id.toString() + ",\r\n"
		+ "	\"rental_id\":\"" + rental_id.toString() + "\",\r\n"
		+ "	\"user_id\":\"" + user_id.toString() + "\",\r\n"
		+ " \"message\":\"" + message + "\",\r\n"
		+ "	\"created_at\":\"" + sdf.format(created_at) +"\",\r\n"
		+ "	\"updated_at\":\"" + sdf.format(updated_at) + "\"\r\n"
		+ "}";
		
	}
	
	
	
}
