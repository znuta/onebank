package com.onebank.onebank.auth.service;

import com.onebank.onebank.auth.dto.output.UserAuthResponseDTO;
import com.onebank.onebank.auth.entity.AppUser;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    UserAuthResponseDTO getUserDetails(String username);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findById(Long id);
}
