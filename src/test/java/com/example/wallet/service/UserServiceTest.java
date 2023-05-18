package com.example.wallet.service;

import com.example.wallet.entity.User;
import com.example.wallet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldUsernameByEmailWorkProperly() {
        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("hjhjkjjkh")
                .enabled(true)
                .locked(false)
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User loadedUser = (User) userService.loadUserByUsername(user.getEmail());

        assertNotNull(loadedUser);
        assertEquals(user, loadedUser);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void shouldUsernameByEmailThrowErrorIfUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("faizbshah2001@gmail.com"));

        assertEquals("User does not exist with email faizbshah2001@gmail.com", exception.getMessage());

        verify(userRepository, times(1)).findByEmail("faizbshah2001@gmail.com");
    }

}