package com.pamarcar.api.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

public class AuthorizationFilter extends BasicAuthenticationFilter {

	private final SecretKey key;

	public AuthorizationFilter(AuthenticationManager manager, SecretKey key) {
		super(manager);
		this.key = key;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

		try {

			String header = request.getHeader("Authorization");

			if (header == null || !header.startsWith("Bearer ")) {

				chain.doFilter(request, response);
				return;

			}

			UsernamePasswordAuthenticationToken authentication = getAuthentication(header);

			if (authentication != null) {

				SecurityContextHolder.getContext().setAuthentication(authentication);

			}

			chain.doFilter(request, response);

		} catch (ExpiredJwtException e) {

			response.setStatus(419);

		}

	}

	private UsernamePasswordAuthenticationToken getAuthentication(String token) throws ExpiredJwtException {

		try {

			String jwt = token.replace("Bearer", "").trim();

			Claims claims = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(jwt)
					.getPayload();

			String user = claims.getSubject();

			@SuppressWarnings("unchecked")
			List<String> roles = (List<String>) claims.get("roles");

			List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", roles));

			return user == null ? null : new UsernamePasswordAuthenticationToken(user, jwt, authorities);

		} catch (SignatureException | ClassCastException e) {

			return null;

		}

	}

}
