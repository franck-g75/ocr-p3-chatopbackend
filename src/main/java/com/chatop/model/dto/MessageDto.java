package com.chatop.model.dto;

import java.text.SimpleDateFormat;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "MessageDto Model")
public class MessageDto {

	private Integer rental_id;
	private Integer user_id;
	private String message;
	
	public MessageDto() {}
	
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

	//--------------------------------------------------------------------------
	
	public String toJson() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		return "{\r\n"
		+ "	\"rental_id\":\"" + rental_id.toString() + "\",\r\n"
		+ "	\"user_id\":\"" + user_id.toString() + "\",\r\n"
		+ " \"message\":\"" + message + "\",\r\n"
		+ "}";
		
	}
	
	
	
}
