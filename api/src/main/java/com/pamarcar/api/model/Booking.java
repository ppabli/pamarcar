package com.pamarcar.api.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.pamarcar.api.util.ValidDateRange;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@ValidDateRange
public class Booking {

	@Id
	@Null(message = "The id field must be empty on create", groups = {OnBookingCreate.class})
	@NotNull(message = "The id field is required on relation", groups = {OnTravelerRegistryCreate.class})
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Null(message = "The security code field must be empty on create", groups = {OnBookingCreate.class})
	@NotNull(message = "The security code field is required on relation", groups = {OnTravelerRegistryCreate.class})
	@Column(name = "security_code", nullable = false, unique = true, length = 36)
	private String securityCode;

	@NotNull(message = "The start date field must not be empty on create", groups = {OnBookingCreate.class})
	@FutureOrPresent(message = "The start date field must be in the present or future")
	@Null(message = "The start date field must be empty on relation", groups = {OnTravelerRegistryCreate.class})
	@Column(nullable = false)
	private Date startDate;

	@NotNull(message = "The end date field must not be empty on create", groups = {OnBookingCreate.class})
	@Null(message = "The end date field must be empty on relation", groups = {OnTravelerRegistryCreate.class})
	@Future(message = "End date field must be in the future")
	@Column(nullable = false)
	private Date endDate;

	@NotNull(message = "The app price per day field must not be empty on create", groups = {OnBookingCreate.class})
	@Null(message = "The app price per day field must be empty on relation", groups = {OnTravelerRegistryCreate.class})
	@DecimalMin(value = "0.00", message = "Price per day field  must be at least 0", groups = {OnBookingCreate.class})
	@DecimalMax(value = "100.00", message = "Price per day field  must be at most 100", groups = {OnBookingCreate.class})
	@Column(name = "app_commission", precision = 5, scale = 2, nullable = false)
	private BigDecimal priceDay;

	@Column(columnDefinition = "TEXT")
	private String comment;

	@NotBlank(message = "The platform number field must not be empty on create", groups = {OnBookingCreate.class})
	@Null(message = "The platform number field must be empty on relation", groups = {OnTravelerRegistryCreate.class})
	@Column(name = "platform_number", nullable = false, unique = true)
	private String platformId;

	@ManyToOne
	@JoinColumn(name = "platform_id", nullable = false)
	@NotNull(message = "The platform field must not be empty on create", groups = {OnBookingCreate.class})
	@Null(message = "The platform field must be empty on relation", groups = {OnTravelerRegistryCreate.class})
	@JsonIncludeProperties(value = {"id"})
	@Valid
	private Platform platform;

	@OneToOne
	@JoinColumn(name = "apartment_id", nullable = false)
	@NotNull(message = "The apartment field must not be empty on create", groups = {OnBookingCreate.class})
	@Null(message = "The apartment field must be empty on relation", groups = {OnTravelerRegistryCreate.class})
	@JsonIncludeProperties(value = {"id"})
	@Valid
	private Apartment apartment;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@NotNull(message = "The user field must not be empty on create", groups = {OnBookingCreate.class})
	@Null(message = "The user field must be empty on relation", groups = {OnTravelerRegistryCreate.class})
	@JsonIncludeProperties(value = {"id"})
	@Valid
	private User user;

	@CreationTimestamp
	@Null(message = "The created at field must be empty on create", groups = {OnBookingCreate.class, OnTravelerRegistryCreate.class})
	@Column(updatable = false, name = "created_at", nullable = false)
	private Date createdAt;

	@UpdateTimestamp
	@Null(message = "The updated at field must be empty on create", groups = {OnBookingCreate.class, OnTravelerRegistryCreate.class})
	@Column(name = "updated_at", nullable = false)
	private Date updatedAt;

	public Booking() {
	}

	public Booking(Long id, String securityCode, Date startDate, Date endDate, BigDecimal priceDay, String comment, String platformId, Platform platform, Apartment apartment, User user, Date createdAt, Date updatedAt) {
		this.id = id;
		this.securityCode = securityCode;
		this.startDate = startDate;
		this.endDate = endDate;
		this.priceDay = priceDay;
		this.comment = comment;
		this.platformId = platformId;
		this.platform = platform;
		this.apartment = apartment;
		this.user = user;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public Booking setId(Long id) {
		this.id = id;
		return this;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public Booking setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Booking setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Booking setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public BigDecimal getPriceDay() {
		return priceDay;
	}

	public Booking setPriceDay(BigDecimal priceDay) {
		this.priceDay = priceDay;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public Booking setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public String getPlatformId() {
		return platformId;
	}

	public Booking setPlatformId(String platformId) {
		this.platformId = platformId;
		return this;
	}

	public Platform getPlatform() {
		return platform;
	}

	public Booking setPlatform(Platform platform) {
		this.platform = platform;
		return this;
	}

	public Apartment getApartment() {
		return apartment;
	}

	public Booking setApartment(Apartment apartment) {
		this.apartment = apartment;
		return this;
	}

	public User getUser() {
		return user;
	}

	public Booking setUser(User user) {
		this.user = user;
		return this;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Booking setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public Booking setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

}
