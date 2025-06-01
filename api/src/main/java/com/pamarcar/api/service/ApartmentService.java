package com.pamarcar.api.service;

import com.pamarcar.api.model.Apartment;
import com.pamarcar.api.model.Result;
import com.pamarcar.api.repository.ApartmentRepository;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class ApartmentService {

	private final ApartmentRepository apartments;
	private final Validator validator;

	@Autowired
	public ApartmentService(ApartmentRepository apartments, Validator validator) {
		this.apartments = apartments;
		this.validator = validator;
	}

	public Result<Page<Apartment>> get(int page, int size, Sort sort, Example<Apartment> filter) {

		Pageable request = PageRequest.of(page, size, sort);

		Page<Apartment> pageResult = apartments.findAll(filter, request);

		return new Result<>(pageResult, false, "Apartments data", 0, Result.Code.OK);

	}

	public Result<Apartment> get(Long id) {

		Apartment result = apartments.findById(id).orElse(null);

		if (result == null) {

			return new Result<>(null, false, "No apartment", 0, Result.Code.NOT_FOUND);

		}

		return new Result<>(result, false, "Apartment data", 0, Result.Code.OK);

	}

}
