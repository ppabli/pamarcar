package com.pamarcar.api.repository;

import com.pamarcar.api.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface GroupRepository extends JpaRepository<Group, Long>, QueryByExampleExecutor<Group> {

}
