package com.pamarcar.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
public class Device {

	@Id
	@Null(message = "The id field must be empty on create", groups = {OnDeviceCreate.class})
	@NotNull(message = "The id field is required on relation", groups = {OnApartmentCreate.class})
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "The identifier field is required on create", groups = {OnDeviceCreate.class})
	@Null(message = "The id field must be empty on create", groups = {OnApartmentCreate.class})
	@Column(nullable = false)
	private String identifier;

	@CreationTimestamp
	@Null(message = "The created at field must be empty on create", groups = {OnDeviceCreate.class, OnApartmentCreate.class})
	@Column(updatable = false, name = "created_at", nullable = false)
	private Date createdAt;

	@UpdateTimestamp
	@Null(message = "The updated at field must be empty on create", groups = {OnDeviceCreate.class, OnApartmentCreate.class})
	@Column(name = "updated_at", nullable = false)
	private Date updatedAt;

	public Device() {
	}

	public Device(Long id, String identifier, Date createdAt, Date updatedAt) {
		this.id = id;
		this.identifier = identifier;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public Device setId(Long id) {
		this.id = id;
		return this;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Device setIdentifier(String identifier) {
		this.identifier = identifier;
		return this;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public Device setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Device setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

}
