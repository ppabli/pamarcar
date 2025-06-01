package com.pamarcar.api.controller;

import com.pamarcar.api.handler.ResponseHandler;
import com.pamarcar.api.model.Platform;
import com.pamarcar.api.model.Result;
import com.pamarcar.api.service.PlatformService;
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
@RequestMapping("platforms")
public class PlatformController {

	private final PlatformService platforms;
	private final LinkRelationProvider relationProvider;

	@Autowired
	public PlatformController(PlatformService platforms, LinkRelationProvider relationProvider) {
		this.platforms = platforms;
		this.relationProvider = relationProvider;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('ADMIN')")
	ResponseEntity<Object> getPlatforms(
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "sort", required = false, defaultValue = "") List<String> sort,
			@RequestParam(name = "name", required = false, defaultValue = "") String name
	) {

		List<Sort.Order> criteria = SortUtil.getCriteria(sort);

		ExampleMatcher matcher = ExampleMatcher
				.matchingAll()
				.withIgnoreCase()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

		Example<Platform> filter = Example.of(
				new Platform().setName(name),
				matcher
		);

		Result<Page<Platform>> result = platforms.get(page, size, Sort.by(criteria), filter);
		ArrayList<Link> links = new ArrayList<>();

		if (result.getResult() != null) {

			Page<Platform> records = result.getResult();
			Pageable metadata = records.getPageable();

			Link self = linkTo(methodOn(PlatformController.class).getPlatforms(page, size, sort, name)).withSelfRel();
			Link first = linkTo(methodOn(PlatformController.class).getPlatforms(metadata.first().getPageNumber(), size, sort, name)).withRel(IanaLinkRelations.FIRST);
			Link last = linkTo(methodOn(PlatformController.class).getPlatforms(records.getTotalPages() - 1, size, sort, name)).withRel(IanaLinkRelations.LAST);
			Link next = linkTo(methodOn(PlatformController.class).getPlatforms(metadata.next().getPageNumber(), size, sort, name)).withRel(IanaLinkRelations.NEXT);
			Link previous = linkTo(methodOn(PlatformController.class).getPlatforms(metadata.previousOrFirst().getPageNumber(), size, sort, name)).withRel(IanaLinkRelations.PREVIOUS);
			Link one = linkTo(methodOn(PlatformController.class).getPlatform(null)).withRel(relationProvider.getItemResourceRelFor(Platform.class));

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
	ResponseEntity<Object> getPlatform(@PathVariable("id") @NotNull Long id) {

		Result<Platform> result = platforms.get(id);
		ArrayList<Link> links = new ArrayList<>();

		if (result.getResult() != null) {

			Link self = linkTo(methodOn(PlatformController.class).getPlatform(id)).withSelfRel();
			Link all = linkTo(PlatformController.class).withRel(relationProvider.getCollectionResourceRelFor(Platform.class));

			links.add(self);
			links.add(all);

		}

		return ResponseHandler.generateResponse(result.isError(), result.getMessaje(), result.getInternalCode(), result.getResult(), links, result.getStatus());

	}

}
