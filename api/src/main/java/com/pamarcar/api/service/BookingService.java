package com.pamarcar.api.service;

import com.pamarcar.api.model.Booking;
import com.pamarcar.api.model.Result;
import com.pamarcar.api.repository.BookingRepository;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BookingService {

	private final BookingRepository bookings;
	private final Validator validator;

	@Autowired
	public BookingService(BookingRepository bookings, Validator validator) {
		this.bookings = bookings;
		this.validator = validator;
	}

	public Result<Page<Booking>> get(int page, int size, Sort sort, Example<Booking> filter) {

		Pageable request = PageRequest.of(page, size, sort);

		Page<Booking> pageResult = bookings.findAll(filter, request);

		return new Result<>(pageResult, false, "Bookings data", 0, Result.Code.OK);

	}

	public Result<Booking> get(Long id) {

		Booking result = bookings.findById(id).orElse(null);

		if (result == null) {

			return new Result<>(null, false, "No booking", 0, Result.Code.NOT_FOUND);

		}

		return new Result<>(result, false, "Booking data", 0, Result.Code.OK);

	}

	public Result<Booking> create(Booking booking) {

		try {

			Booking exist = bookings.findByPlatformId(booking.getPlatformId());

			if (exist != null) {

				return new Result<>(null, false, "Booking already exists", 0, Result.Code.CONFLICT);

			}

			booking.setSecurityCode(UUID.randomUUID().toString());

			Booking new_registry = bookings.save(booking);

			return new Result<>(new_registry, false, "Booking created", 0, Result.Code.CREATED);

		} catch (Exception e) {

			return new Result<>(null, true, e.getLocalizedMessage(), 0, Result.Code.BAD_REQUEST);

		}

	}

}
