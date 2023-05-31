package com.example.wallet.security.provider;

import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import com.example.wallet.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JWTAuthenticationProviderTest {

    @InjectMocks
    private JWTAuthenticationProvider jwtAuthenticationProvider;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAuthenticateWorkProperly() {
        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("helloworld")
                .wallet(new Wallet())
                .enabled(true)
                .locked(false)
                .build();

        Authentication authentication = new  UsernamePasswordAuthenticationToken(user.getEmail(), "raw_password");

        when(userService.loadUserByUsername(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("raw_password", user.getPassword())).thenReturn(true);

        Authentication result = jwtAuthenticationProvider.authenticate(authentication);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals(user.getEmail(), result.getName());
        assertEquals(user.getPassword(), result.getCredentials().toString());
        assertEquals(user.getAuthorities(), result.getAuthorities());

        verify(userService, times(1)).loadUserByUsername(user.getEmail());
        verify(passwordEncoder, times(1)).matches("raw_password", user.getPassword());
    }

    @Test
    void shouldAuthenticateFailIfWrongPassword() {
        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("helloworld")
                .wallet(new Wallet())
                .enabled(true)
                .locked(false)
                .build();

        Authentication authentication = new  UsernamePasswordAuthenticationToken(user.getEmail(), "raw_password");

        when(userService.loadUserByUsername(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("raw_password", user.getPassword())).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> jwtAuthenticationProvider.authenticate(authentication));

        assertEquals("Password does not match", exception.getMessage());

        verify(userService, times(1)).loadUserByUsername(user.getEmail());
        verify(passwordEncoder, times(1)).matches("raw_password", user.getPassword());
    }

    @Test
    void shouldSupportsReturnTrueIfAuthenticationIsOfUsernamePasswordAuthenticationTokenClass() {
        assertTrue(jwtAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldSupportsReturnFalseIfAuthenticationIsNotOfUsernamePasswordAuthenticationTokenClass() {
        assertFalse(jwtAuthenticationProvider.supports(AnonymousAuthenticationToken.class));
    }

}