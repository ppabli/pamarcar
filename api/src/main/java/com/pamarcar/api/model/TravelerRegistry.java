package com.pamarcar.api.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
public class TravelerRegistry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Null(message = "The id field must be empty on create", groups = {OnTravelerRegistryCreate.class})
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booking_id", nullable = false)
	@NotNull(message = "The booking field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@JsonIncludeProperties(value = {"id", "securityCode"})
	@Valid
	private Booking booking;

	@NotNull(message = "The document type field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentType documentType;

	@NotBlank(message = "The document number field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Size(max = 50)
	@Column(nullable = false)
	private String documentNumber;

	@NotNull(message = "The document issued date field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@PastOrPresent(message = "The document issued date field must not be past or present document issued date")
	@Column(nullable = false)
	private Date documentIssuedDate;

	@NotBlank(message = "The document support field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Size(max = 100)
	@Column(nullable = false)
	private String documentSupport;

	@NotBlank(message = "The first name field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Size(max = 100)
	@Column(nullable = false)
	private String firstName;

	@NotBlank(message = "The last name field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Size(max = 100)
	@Column(nullable = false)
	private String lastName;

	@NotNull(message = "The birth date field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Past(message = "The birth date field must not be present or future birth date")
	@Column(nullable = false)
	private Date birthDate;

	@Enumerated(EnumType.STRING)
	@NotNull(message = "The gender field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Column(nullable = false)
	private Gender gender;

	@NotBlank(message = "The nationality field must not be empty on create  on create", groups = {OnTravelerRegistryCreate.class})
	@Size(max = 100)
	@Column(nullable = false)
	private String nationality;

	@NotBlank(message = "The phone field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Pattern(regexp = "\\+?\\d{7,15}", message = "The phone number field must be a valid phone number")
	@Column(nullable = false)
	private String phone;

	@NotBlank(message = "The email field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Email(message = "The email field must be a valid email")
	@Column(nullable = false)
	private String email;

	@NotBlank(message = "The city field must not be empty on createe", groups = {OnTravelerRegistryCreate.class})
	@Size(max = 100)
	@Column(nullable = false)
	private String city;

	@NotBlank(message = "The province field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Size(max = 100)
	@Column(nullable = false)
	private String province;

	@NotBlank(message = "The country field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Size(max = 100)
	@Column(nullable = false)
	private String country;

	@NotBlank(message = "The postal code field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Pattern(regexp = "\\d{4,10}", message = "The postal code field must be a valid postal code.")
	@Column(nullable = false)
	private String postalCode;

	@NotBlank(message = "The signature field must not be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Lob
	@Column(nullable = false)
	private String signature;

	@CreationTimestamp
	@Null(message = "The created at field must be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Column(updatable = false, name = "created_at", nullable = false)
	private Date createdAt;

	@UpdateTimestamp
	@Null(message = "The updated at field must be empty on create", groups = {OnTravelerRegistryCreate.class})
	@Column(name = "updated_at", nullable = false)
	private Date updatedAt;

	public TravelerRegistry() {
	}

	public TravelerRegistry(Long id, Booking booking, DocumentType documentType, String documentNumber, Date documentIssuedDate, String documentSupport, String firstName, String lastName, Date birthDate, Gender gender, String nationality, String phone, String email, String city, String province, String country, String postalCode, String signature, Date createdAt, Date updatedAt) {
		this.id = id;
		this.booking = booking;
		this.documentType = documentType;
		this.documentNumber = documentNumber;
		this.documentIssuedDate = documentIssuedDate;
		this.documentSupport = documentSupport;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.gender = gender;
		this.nationality = nationality;
		this.phone = phone;
		this.email = email;
		this.city = city;
		this.province = province;
		this.country = country;
		this.postalCode = postalCode;
		this.signature = signature;
	}

	public Long getId() {
		return id;
	}

	public TravelerRegistry setId(Long id) {
		this.id = id;
		return this;
	}

	public Booking getBooking() {
		return booking;
	}

	public TravelerRegistry setBooking(Booking booking) {
		this.booking = booking;
		return this;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public TravelerRegistry setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
		return this;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public TravelerRegistry setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
		return this;
	}

	public Date getDocumentIssuedDate() {
		return documentIssuedDate;
	}

	public TravelerRegistry setDocumentIssuedDate(Date documentIssuedDate) {
		this.documentIssuedDate = documentIssuedDate;
		return this;
	}

	public String getDocumentSupport() {
		return documentSupport;
	}

	public TravelerRegistry setDocumentSupport(String documentSupport) {
		this.documentSupport = documentSupport;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public TravelerRegistry setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public TravelerRegistry setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public TravelerRegistry setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
		return this;
	}

	public Gender getGender() {
		return gender;
	}

	public TravelerRegistry setGender(Gender gender) {
		this.gender = gender;
		return this;
	}

	public String getNationality() {
		return nationality;
	}

	public TravelerRegistry setNationality(String nationality) {
		this.nationality = nationality;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public TravelerRegistry setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public TravelerRegistry setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getCity() {
		return city;
	}

	public TravelerRegistry setCity(String city) {
		this.city = city;
		return this;
	}

	public String getProvince() {
		return province;
	}

	public TravelerRegistry setProvince(String province) {
		this.province = province;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public TravelerRegistry setCountry(String country) {
		this.country = country;
		return this;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public TravelerRegistry setPostalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	public String getSignature() {
		return signature;
	}

	public TravelerRegistry setSignature(String signature) {
		this.signature = signature;
		return this;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public TravelerRegistry setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public TravelerRegistry setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

}
