package com.example.wallet.controller;

import com.example.wallet.entity.User;
import com.example.wallet.exception.AppException;
import com.example.wallet.exception.AppExceptionHandler;
import com.example.wallet.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new AppExceptionHandler())
                .build();
    }

    @Test
    void shouldRegisterUserAPIWorkCorrectly() throws Exception {
        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("helloworld")
                .enabled(true)
                .locked(false)
                .build();

        when(authService.registerUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword())).thenReturn(user);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"firstName\": \"Faiz\",\n" +
                                "\t\"lastName\": \"Shah\",\n" +
                                "\t\"email\": \"faizbshah2001@gmail.com\",\n" +
                                "\t\"password\": \"helloworld\"\n" +
                                "}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));

        verify(authService, times(1)).registerUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
    }

    @Test
    void shouldThrowAnErrorIfUserHasAlreadyRegistered() throws Exception {
        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("helloworld")
                .enabled(true)
                .locked(false)
                .build();

        doThrow(new AppException(HttpStatus.BAD_REQUEST, "User Already Exists"))
                .when(authService)
                .registerUser(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPassword()
                );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"firstName\": \"Faiz\",\n" +
                                "\t\"lastName\": \"Shah\",\n" +
                                "\t\"email\": \"faizbshah2001@gmail.com\",\n" +
                                "\t\"password\": \"helloworld\"\n" +
                                "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User Already Exists"));

        verify(authService, times(1)).registerUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
    }

    @Test
    void shouldLoginUserAPIWorkCorrectly() throws Exception {
        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("helloworld")
                .enabled(true)
                .locked(false)
                .build();
        String mockJwtToken = "random_jwt_token";

        when(authService.loginUser(eq(user.getEmail()), eq(user.getPassword()), any(HttpServletRequest.class))).thenReturn(mockJwtToken);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"email\": \"faizbshah2001@gmail.com\",\n" +
                                "\t\"password\": \"helloworld\"\n" +
                                "}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value(mockJwtToken));

        verify(authService, times(1)).loginUser(eq(user.getEmail()), eq(user.getPassword()), any(HttpServletRequest.class));
    }

    @Test
    void shouldThrowAnErrorIfAnUnregisteredUserIsTryingToLogIn() throws Exception {
        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("helloworld")
                .enabled(true)
                .locked(false)
                .build();

        doThrow(new AppException(HttpStatus.NOT_FOUND, "User does not have an account"))
                .when(authService)
                .loginUser(eq(user.getEmail()), eq(user.getPassword()), any(HttpServletRequest.class));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"email\": \"faizbshah2001@gmail.com\",\n" +
                                "\t\"password\": \"helloworld\"\n" +
                                "}")
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User does not have an account"));

        verify(authService, times(1)).loginUser(eq(user.getEmail()), eq(user.getPassword()), any(HttpServletRequest.class));
    }

    @Test
    void shouldThrowAnErrorIfAnUserIsTryingToLogInWhenTheyAreAlreadyLoggedIn() throws Exception {
        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("helloworld")
                .enabled(true)
                .locked(false)
                .build();

        doThrow(new AppException(HttpStatus.BAD_REQUEST, "User already logged in"))
                .when(authService)
                .loginUser(eq(user.getEmail()), eq(user.getPassword()), any(HttpServletRequest.class));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"email\": \"faizbshah2001@gmail.com\",\n" +
                                "\t\"password\": \"helloworld\"\n" +
                                "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User already logged in"));

        verify(authService, times(1)).loginUser(eq(user.getEmail()), eq(user.getPassword()), any(HttpServletRequest.class));
    }

}