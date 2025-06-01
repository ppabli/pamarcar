package com.pamarcar.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Platform {

	@Id
	@Null(message = "The id field must be empty on create", groups = {OnPlatformCreate.class})
	@NotNull(message = "The id field is required on relation", groups = {OnBookingCreate.class})
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "The name field must not be empty on create", groups = {OnPlatformCreate.class})
	@Null(message = "The name field must be empty on relation", groups = {OnBookingCreate.class})
	@Column(name = "name", nullable = false)
	private String name;

	@NotNull(message = "The app commission field must not be empty on create", groups = {OnPlatformCreate.class})
	@Null(message = "The app commission field must be empty on relation", groups = {OnBookingCreate.class})
	@DecimalMin(value = "0.00", message = "The app commission field must be at least 0", groups = {OnPlatformCreate.class})
	@DecimalMax(value = "100.00", message = "The app commission field must be at most 100", groups = {OnPlatformCreate.class})
	@Column(name = "app_commission", precision = 5, scale = 2, nullable = false)
	private BigDecimal appCommission;

	@NotNull(message = "The bank commission field must not be empty on create", groups = {OnPlatformCreate.class})
	@Null(message = "The bank commission field must be empty on relation", groups = {OnBookingCreate.class})
	@DecimalMin(value = "0.00", message = "The bank commission field must be at least 0", groups = {OnPlatformCreate.class})
	@DecimalMax(value = "100.00", message = "The bank commission field must be at most 100", groups = {OnPlatformCreate.class})
	@Column(name = "bank_commission", precision = 5, scale = 2, nullable = false)
	private BigDecimal bankCommission;

	@NotNull(message = "The VAT field must not be empty on create", groups = {OnPlatformCreate.class})
	@Null(message = "The VAT field must be empty on relation", groups = {OnBookingCreate.class})
	@DecimalMin(value = "0.00", message = "The VAT field must be at least 0", groups = {OnPlatformCreate.class})
	@DecimalMax(value = "100.00", message = "The VAT field must be at most 100", groups = {OnPlatformCreate.class})
	@Column(name = "vat", precision = 5, scale = 2, nullable = false)
	private BigDecimal vat;

	@NotNull(message = "The 7-day discount field must not be empty on create", groups = {OnPlatformCreate.class})
	@Null(message = "The 7-day discount field must be empty on relation", groups = {OnBookingCreate.class})
	@DecimalMin(value = "0.00", message = "The 7-day discount field must be at least 0", groups = {OnPlatformCreate.class})
	@DecimalMax(value = "100.00", message = "The 7-day discount field must be at most 100", groups = {OnPlatformCreate.class})
	@Column(name = "discount_7_days", precision = 5, scale = 2, nullable = false)
	private BigDecimal discount7Days;

	@NotNull(message = "The 28-day discount field must not be empty on create", groups = {OnPlatformCreate.class})
	@Null(message = "The 28-day discount field must be empty on relation", groups = {OnBookingCreate.class})
	@DecimalMin(value = "0.00", message = "The 28-day discount field must be at least 0", groups = {OnPlatformCreate.class})
	@DecimalMax(value = "100.00", message = "The 28-day discount field must be at most 100", groups = {OnPlatformCreate.class})
	@Column(name = "discount_28_days", precision = 5, scale = 2, nullable = false)
	private BigDecimal discount28Days;

	@Null(message = "The comment field must be empty on create", groups = {OnBookingCreate.class})
	@Column(columnDefinition = "TEXT")
	private String comment;

	@CreationTimestamp
	@Null(message = "The created at field must be empty on create/relation", groups = {OnPlatformCreate.class, OnBookingCreate.class})
	@Column(updatable = false, name = "created_at", nullable = false)
	private Date createdAt;

	@UpdateTimestamp
	@Null(message = "The updated at field must be empty on create/relation", groups = {OnPlatformCreate.class, OnBookingCreate.class})
	@Column(name = "updated_at", nullable = false)
	private Date updatedAt;

	public Platform() {
	}

	public Platform(Long id, String name, BigDecimal appCommission, BigDecimal bankCommission, BigDecimal vat, BigDecimal discount7Days, BigDecimal discount28Days, String comment, Date createdAt, Date updatedAt) {
		this.id = id;
		this.name = name;
		this.appCommission = appCommission;
		this.bankCommission = bankCommission;
		this.vat = vat;
		this.discount7Days = discount7Days;
		this.discount28Days = discount28Days;
		this.comment = comment;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public Platform setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Platform setName(String name) {
		this.name = name;
		return this;
	}

	public BigDecimal getAppCommission() {
		return appCommission;
	}

	public Platform setAppCommission(BigDecimal appCommission) {
		this.appCommission = appCommission;
		return this;
	}

	public BigDecimal getBankCommission() {
		return bankCommission;
	}

	public Platform setBankCommission(BigDecimal bankCommission) {
		this.bankCommission = bankCommission;
		return this;
	}

	public BigDecimal getVat() {
		return vat;
	}

	public Platform setVat(BigDecimal vat) {
		this.vat = vat;
		return this;
	}

	public BigDecimal getDiscount7Days() {
		return discount7Days;
	}

	public Platform setDiscount7Days(BigDecimal discount7Days) {
		this.discount7Days = discount7Days;
		return this;
	}

	public BigDecimal getDiscount28Days() {
		return discount28Days;
	}

	public Platform setDiscount28Days(BigDecimal discount28Days) {
		this.discount28Days = discount28Days;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public Platform setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Platform setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public Platform setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

}
