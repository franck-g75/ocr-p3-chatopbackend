package com.chatop.model.dto;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DTO to read a rental
 */
public class ReadRentalDto {

	Logger log = LoggerFactory.getLogger(ReadRentalDto.class);
	
	private Integer id;
	private String name;
	private Integer surface;
	private Integer price;
	private String picture;
	private String description;
	private Integer owner_id;
	private Timestamp created_at;
	private Timestamp updated_at;
	
	public ReadRentalDto() {}
	
	public ReadRentalDto(Integer id, String name, Integer surface,	Integer price, String pic, String description , Integer owner_id, Timestamp created_at, Timestamp updated_at) {
		this.id= id;
		this.name = name;
		this.surface = surface;
		this.price = price;
		this.description = description;
		this.picture = pic;
		this.owner_id= owner_id;
		this.created_at = created_at;
		this.updated_at = updated_at;
	}
	
	//------------------------------------------
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSurface() {
		return surface;
	}

	public void setSurface(Integer surface) {
		this.surface = surface;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(Integer owner_id) {
		this.owner_id = owner_id;
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

	
	
	public String toJson() throws IOException {
		
		
		
		Integer jsonId;
		if (this.id == null) {
			jsonId=0;
		} else {
			jsonId=this.id;
		}
		
		Integer jsonOwner_id;
		if (this.owner_id == null) {
			jsonOwner_id=0;
		} else {
			jsonOwner_id=this.owner_id;
		}
	
		String retour = "";
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {
			retour = objectMapper.writeValueAsString( new ReadRentalDto(jsonId,this.name,this.surface,this.price,this.picture,this.description,jsonOwner_id, this.created_at, this.updated_at ) );
		} catch (JsonProcessingException e) {
			log.error("JsonProcessingException");
			throw new IOException("RentalDto JsonProcessingException " + e.toString());
		}
		
		return retour;
	
	}
	
}
