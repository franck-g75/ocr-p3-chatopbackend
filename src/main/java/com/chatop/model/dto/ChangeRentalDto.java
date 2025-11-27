package com.chatop.model.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeRentalDto {

	Logger log = LoggerFactory.getLogger(AddRentalDto.class);
	
	private String name;
	private Integer surface;
	private Integer price;
	private String description;

	
	public ChangeRentalDto() {}
	
	public ChangeRentalDto(String name, Integer surface, Integer price, String description) {
		this.name = name;
		this.surface = surface;
		this.price = price;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
