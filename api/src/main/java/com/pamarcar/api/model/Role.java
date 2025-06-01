package com.pamarcar.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
public class Role {

	@Id
	@Null(message = "The id field must be empty on create", groups = {OnRoleCreate.class})
	@NotNull(message = "The id field must no be empty on relation", groups = {OnUserCreate.class})
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Null(message = "The id field must be empty on relation", groups = {OnUserCreate.class})
	@NotNull(message = "The id field must no be empty on create", groups = {OnRoleCreate.class})
	@Column(nullable = false)
	private String name;

	@CreationTimestamp
	@Null(message = "The created at field must be empty on create", groups = {OnRoleCreate.class})
	@Column(updatable = false, name = "created_at", nullable = false)
	private Date createdAt;

	@UpdateTimestamp
	@Null(message = "The updated at field must be empty on create", groups = {OnRoleCreate.class})
	@Column(name = "updated_at", nullable = false)
	private Date updatedAt;

	public Role() {

	}

	public Role(Long id, String name, Date createdAt, Date updatedAt) {
		this.id = id;
		this.name = name;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public Role setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Role setName(String name) {
		this.name = name;
		return this;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Role setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public Role setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

}
