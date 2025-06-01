package com.pamarcar.api.service;

import com.pamarcar.api.model.Group;
import com.pamarcar.api.model.Result;
import com.pamarcar.api.repository.GroupRepository;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

	private final GroupRepository groups;
	private final Validator validator;

	@Autowired
	public GroupService(GroupRepository groups, Validator validator) {
		this.groups = groups;
		this.validator = validator;
	}

	public Result<Page<Group>> get(int page, int size, Sort sort, Example<Group> filter) {

		Pageable request = PageRequest.of(page, size, sort);

		Page<Group> pageResult = groups.findAll(filter, request);

		return new Result<>(pageResult, false, "Groups data", 0, Result.Code.OK);

	}

	public Result<Group> get(Long id) {

		Group result = groups.findById(id).orElse(null);

		if (result == null) {

			return new Result<>(null, false, "No group", 0, Result.Code.NOT_FOUND);

		}

		return new Result<>(result, false, "Group data", 0, Result.Code.OK);

	}

}
