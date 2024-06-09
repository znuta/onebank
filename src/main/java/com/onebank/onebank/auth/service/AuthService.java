package com.onebank.onebank.auth.service;

import com.onebank.onebank.auth.dto.input.LoginRequest;
import com.onebank.onebank.auth.dto.output.UserAuthResponseDTO;
import com.onebank.onebank.auth.entity.AppUser;

import java.util.Optional;

public interface AuthService {
    UserAuthResponseDTO register(AppUser user);

    UserAuthResponseDTO login(LoginRequest loginInputDTO);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findById(Long id);
}
