package com.pamarcar.api.repository;

import com.pamarcar.api.model.TravelerRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.ArrayList;

public interface TravelerRegistryRepository extends JpaRepository<TravelerRegistry, Long>, QueryByExampleExecutor<TravelerRegistry> {

	ArrayList<TravelerRegistry> findAllByBookingId(Long bookingId);

}
