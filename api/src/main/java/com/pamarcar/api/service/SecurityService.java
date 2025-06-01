package com.pamarcar.api.service;

import com.pamarcar.api.model.User;
import com.pamarcar.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

	private final UserRepository userRepository;

	@Autowired
	public SecurityService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public boolean isSelf(Long id, String email) {

		User user = userRepository.findByEmail(email);

		return user != null && user.getId().equals(id);

	}

}
