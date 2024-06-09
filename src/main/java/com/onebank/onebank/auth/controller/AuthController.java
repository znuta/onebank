package com.onebank.onebank.auth.controller;

import com.onebank.onebank.auth.dto.input.LoginRequest;
import com.onebank.onebank.auth.dto.output.UserAuthResponseDTO;
import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.auth.service.AuthService;
import com.onebank.onebank.auth.utilities.JwtUtil;
import com.onebank.onebank.basicController.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends Controller {

    private AuthService authService;

    private AuthenticationManager authenticationManager;

    private JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authService = authService;

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public UserAuthResponseDTO register(@RequestBody AppUser user) {
      return  updateHttpStatus(authService.register(user));

    }

    @PostMapping("/login")
    public UserAuthResponseDTO login(@RequestBody LoginRequest loginRequest) throws Exception {
        return  updateHttpStatus(authService.login(loginRequest));
    }

}


