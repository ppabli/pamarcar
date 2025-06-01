package com.pamarcar.api.util;

import com.pamarcar.api.model.Booking;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Booking> {

	@Override
	public boolean isValid(Booking booking, ConstraintValidatorContext context) {
		if (booking == null || booking.getStartDate() == null || booking.getEndDate() == null) {
			return true; // Lo validan otras anotaciones como @NotNull
		}
		return booking.getEndDate().after(booking.getStartDate());
	}

}
