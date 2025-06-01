package com.pamarcar.api.handler;

import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseHandler {

	public static ResponseEntity<Object> generateResponse(boolean error, String message, int code, Object responseObj, List<Link> links, HttpStatus status) {

		Map<String, Object> map = new HashMap<>();

		map.put("error", error);
		map.put("code", code);
		map.put("message", message);
		map.put("data", responseObj);

		HttpHeaders headers = new HttpHeaders();

		for (Link link : links) {
			headers.add(HttpHeaders.LINK, link.toString());
		}

		return new ResponseEntity<>(map, headers, status);

	}

}
