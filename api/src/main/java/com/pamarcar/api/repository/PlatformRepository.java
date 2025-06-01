package com.pamarcar.api.repository;

import com.pamarcar.api.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface PlatformRepository extends JpaRepository<Platform, Long>, QueryByExampleExecutor<Platform> {

	Platform findByName(String name);

}
