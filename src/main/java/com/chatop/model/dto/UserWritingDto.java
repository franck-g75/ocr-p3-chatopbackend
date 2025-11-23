package com.chatop.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "UserWritingDto Model")
public class UserWritingDto {

	
	public String email;
	public String name;
	public String password;
	
	//---------------------------------------------------------------------
	
	public UserWritingDto() {}
	
	//---------------------------------------------------------------------
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	//---------------------------------------------------------------------
	
	public String toString() {
		return "userDto:{email=" + email + ", name=" + name + ", password=[disabled]}";
	}
	
}
