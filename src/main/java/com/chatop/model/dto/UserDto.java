package com.chatop.model.dto;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * used to store body authentication and registring request 
 */
public class UserDto {

	private Integer id;
	public String email;
	public String name;
	public String password;
    private Timestamp created_at;
	private Timestamp updated_at;
	
	public UserDto() {}
	
	
	public UserDto(String email, String pwd) {
		this.email = email;
		this.password = pwd;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
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
	
	
	public String toString() {
		return "userDto:{email=" + email + ", name=" + name + ", password=[disabled]}";
	}
	
	public String toJson() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		return "{\r\n"
		+ " \"id\":" + id.toString() + ",\r\n"
		+ "	\"name\":\"" + name + "\",\r\n"
		+ "	\"email\":\"" + email + "\",\r\n"
		+ "	\"created_at\":\"" + sdf.format(created_at) +"\",\r\n"
		+ "	\"updated_at\":\"" + sdf.format(updated_at) + "\"\r\n"
		+ "}";
		
	}
}
