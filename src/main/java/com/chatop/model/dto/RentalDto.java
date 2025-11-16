package com.chatop.model.dto;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class RentalDto {

	private Integer id;
	
	private String name;
	
	private Integer surface;
	
	private Integer price;
	
	private String picture;
	
	private String description;
	
	private Integer owner_id;
	
	private Timestamp created_at;
	
	private Timestamp updated_at;

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
	
	//---------------------------------------------------------------------
	
	public String toJson() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		return "{\r\n"
		+ " \"id\":" + id.toString() + ",\r\n"
		+ "	\"name\":\"" + name + "\",\r\n"
		+ "	\"surface\":\"" + surface.toString() + "\",\r\n"
		+ " \"price\":\"" + price.toString() + "\",\r\n"
		+ " \"picture\":\"" + picture + "\",\r\n"
		+ " \"description\":\"" + description + "\",\r\n"
		+ " \"owner_id\":\"" + owner_id.toString() + "\",\r\n"
		+ "	\"created_at\":\"" + sdf.format(created_at) +"\",\r\n"
		+ "	\"updated_at\":\"" + sdf.format(updated_at) + "\"\r\n"
		+ "}";
		
	}
	
}
