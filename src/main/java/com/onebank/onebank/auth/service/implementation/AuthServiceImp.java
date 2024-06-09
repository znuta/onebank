package com.onebank.onebank.auth.service.implementation;

import com.onebank.onebank.auth.dto.enums.RoleType;
import com.onebank.onebank.auth.dto.input.LoginRequest;
import com.onebank.onebank.auth.dto.output.UserAuthResponseDTO;
import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.auth.repository.UserRepository;
import com.onebank.onebank.auth.service.AuthService;
import com.onebank.onebank.auth.utilities.JwtUtil;
import com.onebank.onebank.auth.utilities.UserUtil;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.exception.BadRequestException;
import com.onebank.onebank.payment.dto.enums.CurrencyType;
import com.onebank.onebank.payment.dto.input.PaymentAccountRequestDto;
import com.onebank.onebank.payment.dto.output.PaymentAccountDTO;
import com.onebank.onebank.payment.service.PaymentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserUtil userUtil;
    private final PaymentAccountService paymentManagerService;

    // Constructor to initialize dependencies
    @Autowired
    public AuthServiceImp(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder, UserUtil userUtil, PaymentAccountService paymentManagerService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userUtil = userUtil;
        this.paymentManagerService = paymentManagerService;
    }

    /**
     * Registers a new user and creates a payment account for them.
     *
     * @param user the user to register
     * @return the response containing user and payment account details along with a JWT token
     */
    public UserAuthResponseDTO register(AppUser user) {
        try {
            // Check if the user already exists
            Optional<AppUser> existingUser = userRepository.findByUsername(user.getUsername());
            if (existingUser.isPresent()) {
                return new UserAuthResponseDTO(Status.BAD_REQUEST, "User with username already exists");
            }

            // Encode the raw password
            String rawPassword = user.getPassword();
            user.setPassword(passwordEncoder.encode(rawPassword));

            // Save the new user
            AppUser newUser = userRepository.save(user);

            // Create a payment account for the new user
            PaymentAccountRequestDto paymentAccountRequestDto = PaymentAccountRequestDto.builder()
                    .currency(CurrencyType.NGN)
                    .name("OneBank Naira")
                    .user(newUser)
                    .build();
            PaymentAccountDTO paymentAccount = paymentManagerService.createPaymentAccount(paymentAccountRequestDto, newUser.getUsername());

            // Authenticate the new user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(newUser.getUsername(), rawPassword,
                            Collections.singleton(new SimpleGrantedAuthority(RoleType.ROLE_USER.getRole()))));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate a JWT token
            String token = jwtUtil.generateToken(newUser);

            // Prepare the response
            Map<String, Object> response = new HashMap<>();
            response.put("user", UserAuthResponseDTO.fromEntity(newUser));
            response.put("bank account", paymentAccount);

            return new UserAuthResponseDTO(Status.SUCCESS, token, response);
        } catch (Exception ex) {
            throw new BadRequestException("Registration failed: " + ex.getMessage());
        }
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param loginInputDTO the login request containing username and password
     * @return the response containing user details and a JWT token
     */
    public UserAuthResponseDTO login(LoginRequest loginInputDTO) {
        Optional<AppUser> existingUser = userRepository.findByUsername(loginInputDTO.getUsername());

        if (!existingUser.isPresent()) {
            return new UserAuthResponseDTO(Status.NOT_FOUND, "User doesn't exist");
        }

        AppUser user = existingUser.get();

        // Check if the password matches
        if (!passwordEncoder.matches(loginInputDTO.getPassword(), user.getPassword())) {
            return new UserAuthResponseDTO(Status.BAD_REQUEST, "Incorrect Password");
        }

        try {
            // Authenticate the user
            Collection<SimpleGrantedAuthority> authorities = userUtil.getAuthority(user);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginInputDTO.getUsername(),
                            loginInputDTO.getPassword(),
                            authorities));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate a JWT token
            String token = jwtUtil.generateToken(user);

            return new UserAuthResponseDTO(Status.SUCCESS, token, UserAuthResponseDTO.fromEntity(user));
        } catch (AuthenticationException ex) {
            throw new BadRequestException("Login failed: " + ex.getMessage());
        }
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return the user, if found
     */
    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the ID to search for
     * @return the user, if found
     */
    public Optional<AppUser> findById(Long id) {
        return userRepository.findById(id);
    }
}
