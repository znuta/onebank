package com.onebank.onebank.auth.service.implementation;

import com.onebank.onebank.auth.dto.enums.RoleType;
import com.onebank.onebank.auth.dto.input.LoginRequest;
import com.onebank.onebank.auth.dto.output.UserAuthResponseDTO;
import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.auth.repository.UserRepository;
import com.onebank.onebank.auth.utilities.JwtUtil;
import com.onebank.onebank.auth.utilities.UserUtil;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.exception.BadRequestException;
import com.onebank.onebank.payment.dto.enums.CurrencyType;
import com.onebank.onebank.payment.dto.input.PaymentAccountRequestDto;
import com.onebank.onebank.payment.dto.output.PaymentAccountDTO;
import com.onebank.onebank.payment.service.PaymentAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserUtil userUtil;

    @Mock
    private PaymentAccountService paymentManagerService;

    @InjectMocks
    private AuthServiceImp authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setPassword("password");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(AppUser.class))).thenReturn(user);
        when(paymentManagerService.createPaymentAccount(any(PaymentAccountRequestDto.class), anyString()))
                .thenReturn(new PaymentAccountDTO());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(jwtUtil.generateToken(any(AppUser.class))).thenReturn("token");

        UserAuthResponseDTO response = authService.register(user);

        assertNotNull(response);
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals("token", response.getToken());

        verify(userRepository, times(1)).save(any(AppUser.class));
        verify(paymentManagerService, times(1)).createPaymentAccount(any(PaymentAccountRequestDto.class), anyString());
    }

    @Test
    void testRegisterUserAlreadyExists() {
        AppUser user = new AppUser();
        user.setUsername("testuser");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserAuthResponseDTO response = authService.register(user);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        assertEquals("User with username already exists", response.getData());

        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    void testLoginSuccess() {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(jwtUtil.generateToken(any(AppUser.class))).thenReturn("token");

        UserAuthResponseDTO response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals("token", response.getToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserAuthResponseDTO response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(Status.NOT_FOUND, response.getStatus());
        assertEquals("User doesn't exist", response.getData());

        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLoginIncorrectPassword() {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        UserAuthResponseDTO response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(Status.BAD_REQUEST, response.getStatus());
        assertEquals("Incorrect Password", response.getData());

        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
