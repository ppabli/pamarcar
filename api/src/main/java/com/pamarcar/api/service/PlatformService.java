package com.pamarcar.api.service;

import com.pamarcar.api.model.Platform;
import com.pamarcar.api.model.Result;
import com.pamarcar.api.repository.PlatformRepository;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class PlatformService {

	private final PlatformRepository platforms;
	private final Validator validator;

	@Autowired
	public PlatformService(PlatformRepository platforms, Validator validator) {
		this.platforms = platforms;
		this.validator = validator;
	}

	public Result<Page<Platform>> get(int page, int size, Sort sort, Example<Platform> filter) {

		Pageable request = PageRequest.of(page, size, sort);

		Page<Platform> pageResult = platforms.findAll(filter, request);

		return new Result<>(pageResult, false, "Platforms data", 0, Result.Code.OK);

	}

	public Result<Platform> get(Long id) {

		Platform result = platforms.findById(id).orElse(null);

		if (result == null) {

			return new Result<>(null, false, "No platform", 0, Result.Code.NOT_FOUND);

		}

		return new Result<>(result, false, "Platform data", 0, Result.Code.OK);

	}

	public Result<Platform> create(Platform platform) {

		try {

			Platform exist = platforms.findByName(platform.getName());

			if (exist != null) {

				return new Result<>(null, false, "Platform already exists", 0, Result.Code.CONFLICT);

			}

			Platform new_registry = platforms.save(platform);

			return new Result<>(new_registry, false, "Platform created", 0, Result.Code.CREATED);

		} catch (Exception e) {

			return new Result<>(null, true, e.getLocalizedMessage(), 0, Result.Code.BAD_REQUEST);

		}

	}

}
