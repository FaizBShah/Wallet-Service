package com.example.wallet.security.jwt;

import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import com.example.wallet.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JWTUtilsTest {

    @InjectMocks
    private JWTUtils jwtUtils;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "test_jwt_secret");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 172800000);
    }

    @Test
    void shouldGenerateJwtTokenWorkProperly() {
        Authentication authentication = mock(Authentication.class);
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

        when(authentication.getPrincipal()).thenReturn(user.getEmail());
        when(userService.loadUserByUsername(user.getEmail())).thenReturn(user);

        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
        assertEquals(user.getEmail(), jwtUtils.validateJwtToken(token));

        verify(authentication, times(1)).getPrincipal();
        verify(userService, times(1)).loadUserByUsername(user.getEmail());
    }

    @Test
    void shouldGeneratePayloadWorkProperly() {
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

        Map<String, String> payload = jwtUtils.generatePayload(user);

        assertNotNull(payload);
        assertEquals(3, payload.size());
        assertTrue(payload.containsKey("firstName"));
        assertEquals(payload.get("firstName"), user.getFirstName());
        assertTrue(payload.containsKey("lastName"));
        assertEquals(payload.get("lastName"), user.getLastName());
        assertTrue(payload.containsKey("email"));
        assertEquals(payload.get("email"), user.getEmail());
    }

    @Test
    void shouldValidateJwtTokenWorkCorrectly() {
        Authentication authentication = mock(Authentication.class);
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

        when(authentication.getPrincipal()).thenReturn(user.getEmail());
        when(userService.loadUserByUsername(user.getEmail())).thenReturn(user);

        String token = jwtUtils.generateJwtToken(authentication);

        assertEquals(user.getEmail(), jwtUtils.validateJwtToken(token));
    }

    @Test
    void shouldValidateJwtTokenReturnNullIfAnInvalidTokenIsPassed() {
        assertNull(jwtUtils.validateJwtToken("random_string"));
    }

    @Test
    void shouldParseJwtTokenWorkCorrectly() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer token");

        assertEquals("token", jwtUtils.parseJwtToken(request));

        verify(request, times(1)).getHeader("Authorization");
    }

    @Test
    void shouldParseJwtTokenReturnNullIfNoAuthHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn(null);

        assertNull(jwtUtils.parseJwtToken(request));

        verify(request, times(1)).getHeader("Authorization");
    }

    @Test
    void shouldParseJwtTokenReturnNullIfAuthHeaderLengthLessThanSeven() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer");

        assertNull(jwtUtils.parseJwtToken(request));

        verify(request, times(1)).getHeader("Authorization");
    }

    @Test
    void shouldParseJwtTokenReturnNullIfAuthHeaderDoesNotStartWithCorrectPrefix() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn("Bear and Lion");

        assertNull(jwtUtils.parseJwtToken(request));

        verify(request, times(1)).getHeader("Authorization");
    }

}