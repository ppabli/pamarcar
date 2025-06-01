package com.pamarcar.api.repository;

import com.pamarcar.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface RoleRepository extends JpaRepository<Role, Long>, QueryByExampleExecutor<Role> {

	Role findByName(String name);

}
