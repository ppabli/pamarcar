package com.pamarcar.api.util;

import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SortUtil {

	public static List<Sort.Order> getCriteria(List<String> sort) {

		return sort.stream().map(string -> {
			if (string.startsWith("+")) {
				return Sort.Order.asc(string.substring(1));
			} else if (string.startsWith("-")) {
				return Sort.Order.desc(string.substring(1));
			} else {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());

	}

}
