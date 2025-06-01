package com.pamarcar.api.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "groups")
public class Group {

	@Id
	@Null(message = "The id field must be empty on create", groups = {OnGroupCreate.class})
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Null(message = "The users field must be empty on create", groups = {OnGroupCreate.class})
	@ManyToMany(fetch = FetchType.EAGER)
	@JsonIncludeProperties(value = {"id", "email"})
	private List<User> users;

	@CreationTimestamp
	@Null(message = "The created at field must be empty on create", groups = {OnGroupCreate.class})
	@Column(updatable = false, name = "created_at", nullable = false)
	private Date createdAt;

	@UpdateTimestamp
	@Null(message = "The updated at field must be empty on create", groups = {OnGroupCreate.class})
	@Column(name = "updated_at", nullable = false)
	private Date updatedAt;

	public Group() {

	}

	public Group(Long id, List<User> users, Date createdAt, Date updatedAt) {
		this.id = id;
		this.users = users;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public Group setId(Long id) {
		this.id = id;
		return this;
	}

	public List<User> getUsers() {
		return users;
	}

	public Group setUsers(List<User> users) {
		this.users = users;
		return this;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Group setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public Group setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

}
