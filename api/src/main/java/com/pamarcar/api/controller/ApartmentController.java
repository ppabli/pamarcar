package com.pamarcar.api.controller;

import com.pamarcar.api.handler.ResponseHandler;
import com.pamarcar.api.model.Apartment;
import com.pamarcar.api.model.Platform;
import com.pamarcar.api.model.Result;
import com.pamarcar.api.service.ApartmentService;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("apartments")
public class ApartmentController {

	private final ApartmentService apartments;
	private final LinkRelationProvider relationProvider;

	@Autowired
	public ApartmentController(ApartmentService apartments, LinkRelationProvider relationProvider) {
		this.apartments = apartments;
		this.relationProvider = relationProvider;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('ADMIN')")
	ResponseEntity<Object> getApartments(
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "sort", required = false, defaultValue = "") List<String> sort,
			@RequestParam(name = "id", required = false, defaultValue = "") Long id
	) {

		List<Sort.Order> criteria = SortUtil.getCriteria(sort);

		ExampleMatcher matcher = ExampleMatcher
				.matchingAll()
				.withIgnoreCase()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

		Example<Apartment> filter = Example.of(
				new Apartment().setId(id),
				matcher
		);

		Result<Page<Apartment>> result = apartments.get(page, size, Sort.by(criteria), filter);
		ArrayList<Link> links = new ArrayList<>();

		if (result.getResult() != null) {

			Page<Apartment> records = result.getResult();
			Pageable metadata = records.getPageable();

			Link self = linkTo(methodOn(ApartmentController.class).getApartments(page, size, sort, id)).withSelfRel();
			Link first = linkTo(methodOn(ApartmentController.class).getApartments(metadata.first().getPageNumber(), size, sort, id)).withRel(IanaLinkRelations.FIRST);
			Link last = linkTo(methodOn(ApartmentController.class).getApartments(records.getTotalPages() - 1, size, sort, id)).withRel(IanaLinkRelations.LAST);
			Link next = linkTo(methodOn(ApartmentController.class).getApartments(metadata.next().getPageNumber(), size, sort, id)).withRel(IanaLinkRelations.NEXT);
			Link previous = linkTo(methodOn(ApartmentController.class).getApartments(metadata.previousOrFirst().getPageNumber(), size, sort, id)).withRel(IanaLinkRelations.PREVIOUS);
			Link one = linkTo(methodOn(ApartmentController.class).getApartment(null)).withRel(relationProvider.getItemResourceRelFor(Apartment.class));

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
	ResponseEntity<Object> getApartment(@PathVariable("id") @NotNull Long id) {

		Result<Apartment> result = apartments.get(id);
		ArrayList<Link> links = new ArrayList<>();

		if (result.getResult() != null) {

			Link self = linkTo(methodOn(ApartmentController.class).getApartment(id)).withSelfRel();
			Link all = linkTo(ApartmentController.class).withRel(relationProvider.getCollectionResourceRelFor(Platform.class));

			links.add(self);
			links.add(all);

		}

		return ResponseHandler.generateResponse(result.isError(), result.getMessaje(), result.getInternalCode(), result.getResult(), links, result.getStatus());

	}

}
