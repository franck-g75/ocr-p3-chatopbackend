package com.chatop.model.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO to add a rental
 */
@Schema(description = "AddRentalDto Model")
public class AddRentalDto {

	Logger log = LoggerFactory.getLogger(AddRentalDto.class);
	
	private String name;
	private Integer surface;
	private Integer price;
	private MultipartFile picture;
	private String description;

	public AddRentalDto() {}

	public AddRentalDto(String name, Integer surface, Integer price, MultipartFile pic, String description , Integer owner_id) {
		this.name = name;
		this.surface = surface;
		this.price = price;
		this.description = description;
		this.picture = pic;
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

	public MultipartFile getPicture() {
		return picture;
	}

	public void setPicture(MultipartFile picture) {
		this.picture = picture;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
