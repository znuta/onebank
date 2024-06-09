package com.onebank.onebank.auth.repository;

import com.onebank.onebank.auth.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {


    Optional<AppUser> findByUsername(String username);
}
