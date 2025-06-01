package com.pamarcar.api.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
public class Apartment {

	@Id
	@Null(message = "The id field must be empty on create", groups = {OnApartmentCreate.class})
	@NotNull(message = "The id field is required on relation", groups = {OnBookingCreate.class})
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "The name field can not be empty on create", groups = {OnApartmentCreate.class})
	@Null(message = "The name field must be empty on relation", groups = {OnBookingCreate.class})
	@Column(nullable = false)
	private String name;

	@OneToOne
	@JoinColumn(name = "owner_id", nullable = false)
	@NotNull(message = "The owner field must not be empty on create", groups = {OnApartmentCreate.class})
	@Null(message = "The owner field must be empty on relation", groups = {OnBookingCreate.class})
	@JsonIncludeProperties(value = {"id"})
	@Valid
	private User owner;

	@ManyToMany(fetch = FetchType.EAGER)
	@Valid
	private List<Device> devices;

	@CreationTimestamp
	@Null(message = "The created at field must be empty on create", groups = {OnBookingCreate.class, OnApartmentCreate.class})
	@Column(updatable = false, name = "created_at", nullable = false)
	private Date createdAt;

	@UpdateTimestamp
	@Null(message = "The updated at field must be empty on create", groups = {OnBookingCreate.class, OnApartmentCreate.class})
	@Column(name = "updated_at", nullable = false)
	private Date updatedAt;

	public Apartment() {

	}

	public Apartment(Long id, String name, User owner, List<Device> devices, Date createdAt, Date updatedAt) {
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.devices = devices;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public Apartment setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Apartment setName(String name) {
		this.name = name;
		return this;
	}

	public User getOwner() {
		return owner;
	}

	public Apartment setOwner(User owner) {
		this.owner = owner;
		return this;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public Apartment setDevices(List<Device> devices) {
		this.devices = devices;
		return this;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Apartment setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public Apartment setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

}
