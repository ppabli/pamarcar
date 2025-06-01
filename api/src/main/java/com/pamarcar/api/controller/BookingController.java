package com.pamarcar.api.controller;

import com.pamarcar.api.handler.ResponseHandler;
import com.pamarcar.api.model.Booking;
import com.pamarcar.api.model.OnBookingCreate;
import com.pamarcar.api.model.Platform;
import com.pamarcar.api.model.Result;
import com.pamarcar.api.service.BookingService;
import com.pamarcar.api.util.SortUtil;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("bookings")
public class BookingController {

	private final BookingService bookings;
	private final LinkRelationProvider relationProvider;

	@Autowired
	public BookingController(BookingService bookings, LinkRelationProvider relationProvider) {
		this.bookings = bookings;
		this.relationProvider = relationProvider;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('ADMIN')")
	ResponseEntity<Object> getBookings(
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "sort", required = false, defaultValue = "") List<String> sort,
			@RequestParam(name = "id", required = false, defaultValue = "") Long id,
			@RequestParam(name = "platformId", required = false, defaultValue = "") String platformId
	) {

		List<Sort.Order> criteria = SortUtil.getCriteria(sort);

		ExampleMatcher matcher = ExampleMatcher
				.matchingAll()
				.withIgnoreCase()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

		Example<Booking> filter = Example.of(
				new Booking().setId(id).setPlatformId(platformId),
				matcher
		);

		Result<Page<Booking>> result = bookings.get(page, size, Sort.by(criteria), filter);
		ArrayList<Link> links = new ArrayList<>();

		if (result.getResult() != null) {

			Page<Booking> records = result.getResult();
			Pageable metadata = records.getPageable();

			Link self = linkTo(methodOn(BookingController.class).getBookings(page, size, sort, id, platformId)).withSelfRel();
			Link first = linkTo(methodOn(BookingController.class).getBookings(metadata.first().getPageNumber(), size, sort, id, platformId)).withRel(IanaLinkRelations.FIRST);
			Link last = linkTo(methodOn(BookingController.class).getBookings(records.getTotalPages() - 1, size, sort, id, platformId)).withRel(IanaLinkRelations.LAST);
			Link next = linkTo(methodOn(BookingController.class).getBookings(metadata.next().getPageNumber(), size, sort, id, platformId)).withRel(IanaLinkRelations.NEXT);
			Link previous = linkTo(methodOn(BookingController.class).getBookings(metadata.previousOrFirst().getPageNumber(), size, sort, id, platformId)).withRel(IanaLinkRelations.PREVIOUS);
			Link one = linkTo(methodOn(BookingController.class).getBooking(null)).withRel(relationProvider.getItemResourceRelFor(Booking.class));

			links.add(self);
			links.add(first);
			links.add(last);
			links.add(next);
			links.add(previous);
			links.add(one);

		}

		return ResponseHandler.generateResponse(result.isError(), result.getMessaje(), result.getInternalCode(), result.getResult().stream().toList(), links, result.getStatus());

	}

	@GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('ADMIN')")
	ResponseEntity<Object> getBooking(@PathVariable("id") @NotNull Long id) {

		Result<Booking> result = bookings.get(id);
		ArrayList<Link> links = new ArrayList<>();

		if (result.getResult() != null) {

			Link self = linkTo(methodOn(BookingController.class).getBooking(id)).withSelfRel();
			Link all = linkTo(BookingController.class).withRel(relationProvider.getCollectionResourceRelFor(Platform.class));

			links.add(self);
			links.add(all);

		}

		return ResponseHandler.generateResponse(result.isError(), result.getMessaje(), result.getInternalCode(), result.getResult(), links, result.getStatus());

	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('ADMIN')")
	ResponseEntity<Object> createBooking(@Validated(OnBookingCreate.class) @RequestBody Booking booking) {

		Result<Booking> result = bookings.create(booking);
		ArrayList<Link> links = new ArrayList<>();

		if (result.getResult() != null) {

			Link self = linkTo(methodOn(BookingController.class).getBooking(result.getResult().getId())).withSelfRel();
			Link all = linkTo(BookingController.class).withRel(relationProvider.getCollectionResourceRelFor(Booking.class));

			links.add(self);
			links.add(all);

		}

		return ResponseHandler.generateResponse(result.isError(), result.getMessaje(), result.getInternalCode(), result.getResult(), links, result.getStatus());

	}

}
