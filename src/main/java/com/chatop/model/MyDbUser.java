package com.chatop.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name="users")
public class MyDbUser {

	//Logger log = LoggerFactory.getLogger(MyDbUser.class);
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) //identity : pas de sequences
	private Integer id;

	private String email;
	
	private String name;
	
	private String password;
	
	private Timestamp created_at;
	
	private Timestamp updated_at;
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
