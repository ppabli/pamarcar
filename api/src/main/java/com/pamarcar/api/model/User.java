package com.pamarcar.api.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

	@Id
	@Null(message = "The id field must be empty on create", groups = {OnUserCreate.class})
	@NotNull(message = "The id field must no be empty on relation", groups = {OnBookingCreate.class, OnApartmentCreate.class, OnGroupCreate.class})
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "The email field can not be empty on create", groups = {OnUserCreate.class})
	@Null(message = "The email field must be empty on relation", groups = {OnBookingCreate.class, OnApartmentCreate.class, OnGroupCreate.class})
	@Email(message = "The email field need to be a valid email")
	@Column(nullable = false)
	private String email;

	@NotBlank(message = "The name field can not be empty on create", groups = {OnUserCreate.class})
	@Null(message = "The name field must be empty on relation", groups = {OnBookingCreate.class, OnApartmentCreate.class, OnGroupCreate.class})
	@Column(nullable = false)
	private String name;

	@NotBlank(message = "The password field can not be empty on create", groups = {OnUserCreate.class})
	@Null(message = "The password field must be empty on relation", groups = {OnBookingCreate.class, OnApartmentCreate.class, OnGroupCreate.class})
	@Column(nullable = false)
	private String password;

	@Null(message = "The roles field must be empty on create", groups = {OnUserCreate.class, OnBookingCreate.class, OnApartmentCreate.class, OnGroupCreate.class})
	@ManyToMany(fetch = FetchType.EAGER)
	@JsonIncludeProperties(value = {"id", "name"})
	private List<Role> roles;

	@CreationTimestamp
	@Null(message = "The created at field must be empty on create", groups = {OnUserCreate.class, OnBookingCreate.class, OnApartmentCreate.class, OnGroupCreate.class})
	@Column(updatable = false, name = "created_at", nullable = false)
	private Date createdAt;

	@UpdateTimestamp
	@Null(message = "The updated at field must be empty on create", groups = {OnUserCreate.class, OnBookingCreate.class, OnApartmentCreate.class, OnGroupCreate.class})
	@Column(name = "updated_at", nullable = false)
	private Date updatedAt;

	public User() {
	}

	public User(Long id, String email, String name, String password, List<Role> roles, Date createdAt, Date updatedAt) {
		this.id = id;
		this.email = email;
		this.name = name;
		this.password = password;
		this.roles = roles;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public User setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public User setName(String name) {
		this.name = name;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public User setRoles(List<Role> roles) {
		this.roles = roles;
		return this;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public User setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public User setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

}
