package com.chatop.model.dto;

/**
 * used to log in
 */
public class LoginDto {
	
	private String email;
	private String password;
	
	public LoginDto() {}

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
	
	
}
