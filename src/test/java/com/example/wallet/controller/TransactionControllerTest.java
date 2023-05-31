package com.example.wallet.controller;

import com.example.wallet.entity.*;
import com.example.wallet.exception.AppException;
import com.example.wallet.exception.AppExceptionHandler;
import com.example.wallet.service.TransactionService;
import com.example.wallet.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(transactionController)
                .setControllerAdvice(new AppExceptionHandler())
                .build();
    }

    @Test
    void shouldGetAllTransactionsWorkCorrectly() throws Exception {
        Principal principal = () -> "testUser";

        Wallet wallet = Wallet.builder()
                .id(1L)
                .amount(100.0)
                .currency(Currency.RUPEE)
                .isActivated(true)
                .build();

        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("helloworld")
                .wallet(wallet)
                .enabled(true)
                .locked(false)
                .build();

        Transaction transaction1 = Transaction.builder()
                .id(1L)
                .fromWalletId(1L)
                .fromWalletAmount(5.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(2L)
                .toWalletAmount(5.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.TRANSFER)
                .createdAt(LocalDateTime.of(2023, 1, 1, 10, 10))
                .build();

        Transaction transaction2 = Transaction.builder()
                .id(2L)
                .fromWalletId(1L)
                .fromWalletAmount(5.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(2L)
                .toWalletAmount(5.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.TRANSFER)
                .createdAt(LocalDateTime.of(2023, 1, 1, 11, 11))
                .build();

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);
        when(transactionService.getAllTransactions(user)).thenReturn(List.of(transaction1, transaction2));

        mockMvc.perform(get("/api/v1/transactions")
                        .principal(principal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].fromWalletId").value(1L))
                .andExpect(jsonPath("$[0].fromWalletAmount").value(5.0))
                .andExpect(jsonPath("$[0].fromWalletCurrency").value(Currency.RUPEE.toString()))
                .andExpect(jsonPath("$[0].toWalletId").value(2L))
                .andExpect(jsonPath("$[0].toWalletAmount").value(5.0))
                .andExpect(jsonPath("$[0].toWalletCurrency").value(Currency.RUPEE.toString()))
                .andExpect(jsonPath("$[0].transactionType").value(TransactionType.TRANSFER.toString()))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].fromWalletId").value(1L))
                .andExpect(jsonPath("$[1].fromWalletAmount").value(5.0))
                .andExpect(jsonPath("$[1].fromWalletCurrency").value(Currency.RUPEE.toString()))
                .andExpect(jsonPath("$[1].toWalletId").value(2L))
                .andExpect(jsonPath("$[1].toWalletAmount").value(5.0))
                .andExpect(jsonPath("$[1].toWalletCurrency").value(Currency.RUPEE.toString()))
                .andExpect(jsonPath("$[1].transactionType").value(TransactionType.TRANSFER.toString()));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(transactionService, times(1)).getAllTransactions(user);
    }

    @Test
    void shouldGetAllTransactionsThrowExceptionIfUserWalletIsNotActivatedYet() throws Exception {
        Principal principal = () -> "testUser";

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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);

        doThrow(new AppException(HttpStatus.BAD_REQUEST, "User's wallet is not activated yet"))
                .when(transactionService)
                .getAllTransactions(user);

        mockMvc.perform(get("/api/v1/transactions")
                        .principal(principal)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User's wallet is not activated yet"));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(transactionService, times(1)).getAllTransactions(user);
    }

}