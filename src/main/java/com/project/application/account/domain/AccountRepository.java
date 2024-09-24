package com.project.application.account.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
	Optional<AccountEntity> findByEmail(String email);
}
