package com.pamarcar.api.service;

import com.pamarcar.api.model.Role;
import com.pamarcar.api.model.User;
import com.pamarcar.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AuthenticationService implements UserDetailsService {

	private final UserRepository users;

	@Autowired
	public AuthenticationService(UserRepository users) {

		this.users = users;

	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = users.findByEmail(email);

		if (user == null) {

			throw new UsernameNotFoundException(email);

		}

		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getEmail())
				.password(user.getPassword())
				.authorities(AuthorityUtils.commaSeparatedStringToAuthorityList(
						user.getRoles().stream()
								.map(Role::getName)
								.collect(Collectors.joining(","))
				))
				.build();

	}

}
