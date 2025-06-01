package com.pamarcar.api.controller;

import com.pamarcar.api.handler.ResponseHandler;
import com.pamarcar.api.model.OnUserCreate;
import com.pamarcar.api.model.Result;
import com.pamarcar.api.model.User;
import com.pamarcar.api.service.UserService;
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
@RequestMapping("users")
public class UserController {

	private final UserService users;
	private final LinkRelationProvider relationProvider;

	@Autowired
	public UserController(UserService users, LinkRelationProvider relationProvider) {
		this.users = users;
		this.relationProvider = relationProvider;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('ADMIN')")
	ResponseEntity<Object> getUsers(
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "sort", required = false, defaultValue = "") List<String> sort,
			@RequestParam(name = "email", required = false, defaultValue = "") String email,
			@RequestParam(name = "name", required = false, defaultValue = "") String name
	) {

		List<Sort.Order> criteria = SortUtil.getCriteria(sort);

		ExampleMatcher matcher = ExampleMatcher
				.matchingAll()
				.withIgnoreCase()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

		Example<User> filter = Example.of(
				new User().setEmail(email).setName(name),
				matcher
		);

		Result<Page<User>> result = users.get(page, size, Sort.by(criteria), filter);
		ArrayList<Link> links = new ArrayList<>();

		if (result.getResult() != null) {

			Page<User> records = result.getResult();
			Pageable metadata = records.getPageable();

			Link self = linkTo(methodOn(UserController.class).getUsers(page, size, sort, email, name)).withSelfRel();
			Link first = linkTo(methodOn(UserController.class).getUsers(metadata.first().getPageNumber(), size, sort, email, name)).withRel(IanaLinkRelations.FIRST);
			Link last = linkTo(methodOn(UserController.class).getUsers(records.getTotalPages() - 1, size, sort, email, name)).withRel(IanaLinkRelations.LAST);
			Link next = linkTo(methodOn(UserController.class).getUsers(metadata.next().getPageNumber(), size, sort, email, name)).withRel(IanaLinkRelations.NEXT);
			Link previous = linkTo(methodOn(UserController.class).getUsers(metadata.previousOrFirst().getPageNumber(), size, sort, email, name)).withRel(IanaLinkRelations.PREVIOUS);
			Link one = linkTo(methodOn(UserController.class).getUser(null)).withRel(relationProvider.getItemResourceRelFor(User.class));

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
	@PreAuthorize("hasAuthority('ADMIN') or @securityService.isSelf(#id, principal)")
	ResponseEntity<Object> getUser(@PathVariable("id") @NotNull Long id) {

		Result<User> result = users.get(id);
		ArrayList<Link> links = new ArrayList<>();

		if (result.getResult() != null) {

			Link self = linkTo(methodOn(UserController.class).getUser(id)).withSelfRel();
			Link all = linkTo(UserController.class).withRel(relationProvider.getCollectionResourceRelFor(User.class));

			links.add(self);
			links.add(all);

		}

		return ResponseHandler.generateResponse(result.isError(), result.getMessaje(), result.getInternalCode(), result.getResult(), links, result.getStatus());

	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Object> createUser(@Validated(OnUserCreate.class) @RequestBody User user) {

		Result<User> result = users.create(user);
		ArrayList<Link> links = new ArrayList<>();

		if (result.getResult() != null) {

			Link self = linkTo(methodOn(UserController.class).getUser(result.getResult().getId())).withSelfRel();
			Link all = linkTo(UserController.class).withRel(relationProvider.getCollectionResourceRelFor(User.class));

			links.add(self);
			links.add(all);

		}

		return ResponseHandler.generateResponse(result.isError(), result.getMessaje(), result.getInternalCode(), result.getResult(), links, result.getStatus());

	}

}
