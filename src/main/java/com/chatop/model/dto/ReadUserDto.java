package com.chatop.model.dto;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReadUserDto {

		Logger log = LoggerFactory.getLogger(ReadUserDto.class);
	
		private Integer id;
		public String email;
		public String name;
		public String password;
	    private Timestamp created_at;
		private Timestamp updated_at;
		
		//---------------------------------------------------------------------
		
		public ReadUserDto() {}
		
		//---------------------------------------------------------------------
	
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
		
		//---------------------------------------------------------------------
		
		public String toString() {
			return "userDto:{email=" + email + ", name=" + name + ", password=[disabled]}";
		}
		
		public String toJson() throws IOException {
			
			String retour = "";
			ObjectMapper objectMapper = new ObjectMapper();
			
			try {
				retour = objectMapper.writeValueAsString( this );
			} catch (JsonProcessingException e) {
				log.error("JsonProcessingException");
				throw new IOException("RentalDto JsonProcessingException " + e.toString());
			}
			
			return retour;
		
		}
		
	}
