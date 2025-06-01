package com.pamarcar.api.repository;

import com.pamarcar.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface UserRepository extends JpaRepository<User, Long>, QueryByExampleExecutor<User> {

	User findByEmail(String email);

}
