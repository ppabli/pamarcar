package com.pamarcar.api.repository;

import com.pamarcar.api.model.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface ApartmentRepository extends JpaRepository<Apartment, Long>, QueryByExampleExecutor<Apartment> {

}
