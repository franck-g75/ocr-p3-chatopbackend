package com.chatop.model;

import java.sql.Timestamp;
import com.chatop.model.dto.RentalDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Entity @Table(name="rentals")
public class Rental {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Size(min = 1, max = 130)
	private String name;
	
	@Min(value=0)
	private Integer surface;
	
	@Min(value=0)
	private Integer price;
	
	private String picture;
	
	@Size(min = 1, max = 2000)
	private String description;
	
	@OneToOne
	@JoinColumn(name = "owner_id")
	private MyDbUser owner;
	
	private Timestamp created_at;
	
	private Timestamp updated_at;

	
	
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

	public MyDbUser getOwner() {
		return owner;
	}

	public void setOwner(MyDbUser owner) {
		this.owner = owner;
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
	
	/**
	 * 
	 * @return the RentalDto object for this rental
	 */
	public RentalDto toDto() {
		RentalDto retour = new RentalDto();
		retour.setId(this.id);
		retour.setName(this.name);
		retour.setSurface(this.surface);
		retour.setPrice(this.price);
		retour.setPicture(this.picture);
		retour.setDescription(this.description);
		retour.setOwner_id(this.owner.getId());
		retour.setCreated_at(this.created_at);
		retour.setUpdated_at(this.updated_at);
		return retour;
	}
	
}
