package com.pamarcar.api.service;

import com.pamarcar.api.model.Result;
import com.pamarcar.api.model.Role;
import com.pamarcar.api.model.User;
import com.pamarcar.api.repository.RoleRepository;
import com.pamarcar.api.repository.UserRepository;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {

	private final UserRepository users;
	private final RoleRepository roles;
	private final Validator validator;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserService(UserRepository users, RoleRepository roles, Validator validator, PasswordEncoder encoder) {
		this.users = users;
		this.roles = roles;
		this.validator = validator;
		this.passwordEncoder = encoder;
	}

	public Result<Page<User>> get(int page, int size, Sort sort, Example<User> filter) {

		Pageable request = PageRequest.of(page, size, sort);

		Page<User> pageResult = users.findAll(filter, request);

		return new Result<>(pageResult, false, "Users data", 0, Result.Code.OK);

	}

	public Result<User> get(Long id) {

		User result = users.findById(id).orElse(null);

		if (result == null) {

			return new Result<>(null, false, "No user", 0, Result.Code.NOT_FOUND);

		}

		return new Result<>(result, false, "User data", 0, Result.Code.OK);

	}

	public Result<User> create(User user) {

		try {

			User exist = users.findByEmail(user.getEmail());

			if (exist != null) {

				return new Result<>(null, false, "User already exists", 0, Result.Code.CONFLICT);

			}

			ArrayList<Role> roles = new ArrayList<>();
			Role defaultRole = this.roles.findByName("USER");
			roles.add(defaultRole);

			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setRoles(roles);

			User new_registry = users.save(user);

			return new Result<>(new_registry, false, "User created", 0, Result.Code.CREATED);

		} catch (Exception e) {

			return new Result<>(null, true, e.getLocalizedMessage(), 0, Result.Code.BAD_REQUEST);

		}

	}

}
