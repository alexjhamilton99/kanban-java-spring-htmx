package com.alexjhamilton.kanban.security;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SecurityUserRepository extends CrudRepository<SecurityUser, Long> {

    Optional<SecurityUser> findByEmail(String email);

}
