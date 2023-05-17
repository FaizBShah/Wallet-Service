package com.example.wallet.service;

import com.example.wallet.entity.User;
import com.example.wallet.exception.AppException;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.security.jwt.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTUtils jwtUtils;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail")
                .password("hjhjkjjkh")
                .enabled(true)
                .locked(false)
                .build();
    }

    @Test
    void shouldRegisterUserWorkProperly() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = authService.registerUser("Faiz", "Shah", "faizbshah2001@gmail.com", "hjhjkjjkh");

        assertNotNull(registeredUser);
        assertEquals(user, registeredUser);

        verify(userRepository, times(1)).findByEmail("faizbshah2001@gmail.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowErrorIfUserIsAlreadyRegistered() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        AppException exception = assertThrows(AppException.class, () -> authService.registerUser(
                "Faiz",
                "Shah",
                "faizbshah2001@gmail.com",
                "hjhjkjjkh"
        ));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User Already Exists", exception.getMessage());

        verify(userRepository, times(1)).findByEmail("faizbshah2001@gmail.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginUserWorkProperly() {
        Authentication mockAuthentication = mock(Authentication.class);
        String jwtToken = "eybghttruq";

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);
        when(jwtUtils.generateJwtToken(mockAuthentication)).thenReturn(jwtToken);

        String generatedToken = authService.loginUser(user.getEmail(), user.getPassword());

        assertEquals(jwtToken, generatedToken);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(mockAuthentication);
    }

    @Test
    void shouldThrowAnErrorIfUserDoesNotHaveAnyAccount() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> authService.loginUser(user.getEmail(), user.getPassword()));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User does not have an account", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, never()).generateJwtToken(any(Authentication.class));
    }

}