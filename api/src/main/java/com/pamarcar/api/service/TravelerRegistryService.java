package com.pamarcar.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pamarcar.api.configuration.RabbitMQConfiguration;
import com.pamarcar.api.model.Booking;
import com.pamarcar.api.model.Result;
import com.pamarcar.api.model.TravelerRegistry;
import com.pamarcar.api.repository.BookingRepository;
import com.pamarcar.api.repository.TravelerRegistryRepository;
import com.pamarcar.api.util.MessageSender;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TravelerRegistryService {

	private final TravelerRegistryRepository registries;
	private final BookingRepository bookings;
	private final Validator validator;
	private final MessageSender messageSender;
	private final ObjectMapper mapper;

	@Autowired
	public TravelerRegistryService(TravelerRegistryRepository registries, BookingRepository bookings, Validator validator, MessageSender messageSender, ObjectMapper mapper) {
		this.registries = registries;
		this.bookings = bookings;
		this.validator = validator;
		this.messageSender = messageSender;
		this.mapper = mapper;
	}

	public Result<Page<TravelerRegistry>> get(int page, int size, Sort sort, Example<TravelerRegistry> filter) {

		Pageable request = PageRequest.of(page, size, sort);

		Page<TravelerRegistry> pageResult = registries.findAll(filter, request);

		return new Result<>(pageResult, false, "Traveler registries data", 0, Result.Code.OK);

	}

	public Result<TravelerRegistry> get(Long id) {

		TravelerRegistry result = registries.findById(id).orElse(null);

		if (result == null) {

			return new Result<>(null, false, "No traveler registry", 0, Result.Code.NOT_FOUND);

		}

		return new Result<>(result, false, "Traveler registry data", 0, Result.Code.OK);

	}

	@Transactional
	public Result<TravelerRegistry> create(TravelerRegistry registry) {

		try {

			Booking booking = bookings.findByIdAndSecurityCode(registry.getBooking().getId(), registry.getBooking().getSecurityCode());

			if (booking == null) {
				return new Result<>(null, false, "Booking not found", 0, Result.Code.NOT_FOUND);
			}

			TravelerRegistry new_registry = registries.save(registry);

			ArrayList<TravelerRegistry> travelerRegistries = registries.findAllByBookingId(booking.getId());

			// Send message to create access link
			if (travelerRegistries.size() == 1) {

				messageSender.send(RabbitMQConfiguration.CREATE_ACCESS_QUEUE, mapper.writeValueAsString(booking));

			}

			return new Result<>(new_registry, false, "Traveler registry created", 0, Result.Code.CREATED);

		} catch (Exception e) {

			e.printStackTrace();
			return new Result<>(null, true, e.getLocalizedMessage(), 0, Result.Code.BAD_REQUEST);

		}

	}

}
