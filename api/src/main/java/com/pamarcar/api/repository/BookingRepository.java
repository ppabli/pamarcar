package com.pamarcar.api.repository;

import com.pamarcar.api.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface BookingRepository extends JpaRepository<Booking, Long>, QueryByExampleExecutor<Booking> {

	Booking findByPlatformId(String id);

	Booking findByIdAndSecurityCode(Long id, String securityCode);

}
