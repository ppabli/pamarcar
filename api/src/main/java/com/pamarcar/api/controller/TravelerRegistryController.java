package com.pamarcar.api.controller;

import com.pamarcar.api.handler.ResponseHandler;
import com.pamarcar.api.model.OnTravelerRegistryCreate;
import com.pamarcar.api.model.Result;
import com.pamarcar.api.model.TravelerRegistry;
import com.pamarcar.api.service.TravelerRegistryService;
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
@RequestMapping("registries")
public class TravelerRegistryController {

	private final TravelerRegistryService registries;
	private final LinkRelationProvider relationProvider;

	@Autowired
	public TravelerRegistryController(TravelerRegistryService registries, LinkRelationProvider relationProvider) {
		this.registries = registries;
		this.relationProvider = relationProvider;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('ADMIN')")
	ResponseEntity<Object> getTravelersRegistries(
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "sort", required = false, defaultValue = "") List<String> sort
	) {

		List<Sort.Order> criteria = SortUtil.getCriteria(sort);

		ExampleMatcher matcher = ExampleMatcher
				.matchingAll()
				.withIgnoreCase()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

		Example<TravelerRegistry> filter = Example.of(
				new TravelerRegistry(),
				matcher
		);

		Result<Page<TravelerRegistry>> result = registries.get(page, size, Sort.by(criteria), filter);
		ArrayList<Link> links = new ArrayList<>();

		if (result.getResult() != null) {

			Page<TravelerRegistry> records = result.getResult();
			Pageable metadata = records.getPageable();

			Link self = linkTo(methodOn(TravelerRegistryController.class).getTravelersRegistries(page, size, sort)).withSelfRel();
			Link first = linkTo(methodOn(TravelerRegistryController.class).getTravelersRegistries(metadata.first().getPageNumber(), size, sort)).withRel(IanaLinkRelations.FIRST);
			Link last = linkTo(methodOn(TravelerRegistryController.class).getTravelersRegistries(records.getTotalPages() - 1, size, sort)).withRel(IanaLinkRelations.LAST);
			Link next = linkTo(methodOn(TravelerRegistryController.class).getTravelersRegistries(metadata.next().getPageNumber(), size, sort)).withRel(IanaLinkRelations.NEXT);
			Link previous = linkTo(methodOn(TravelerRegistryController.class).getTravelersRegistries(metadata.previousOrFirst().getPageNumber(), size, sort)).withRel(IanaLinkRelations.PREVIOUS);
			Link one = linkTo(methodOn(TravelerRegistryController.class).getTravelersRegistry(null)).withRel(relationProvider.getItemResourceRelFor(TravelerRegistry.class));

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
	ResponseEntity<Object> getTravelersRegistry(@PathVariable("id") @NotNull Long id) {

		Result<TravelerRegistry> result = registries.get(id);
		ArrayList<Link> links = new ArrayList<>();

		if (result.getResult() != null) {

			Link self = linkTo(methodOn(TravelerRegistryController.class).getTravelersRegistry(id)).withSelfRel();
			Link all = linkTo(TravelerRegistryController.class).withRel(relationProvider.getCollectionResourceRelFor(TravelerRegistry.class));

			links.add(self);
			links.add(all);

		}

		return ResponseHandler.generateResponse(result.isError(), result.getMessaje(), result.getInternalCode(), result.getResult(), links, result.getStatus());

	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Object> createTravelerRegistry(@Validated(OnTravelerRegistryCreate.class) @RequestBody TravelerRegistry registry) {

		Result<TravelerRegistry> result = registries.create(registry);
		ArrayList<Link> links = new ArrayList<>();

		if (result.getResult() != null) {

			Link self = linkTo(methodOn(TravelerRegistryController.class).getTravelersRegistry(result.getResult().getId())).withSelfRel();
			Link all = linkTo(TravelerRegistryController.class).withRel(relationProvider.getCollectionResourceRelFor(TravelerRegistry.class));

			links.add(self);
			links.add(all);

		}

		return ResponseHandler.generateResponse(result.isError(), result.getMessaje(), result.getInternalCode(), result.getResult(), links, result.getStatus());

	}

}
