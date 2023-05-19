package com.example.wallet.controller;

import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import com.example.wallet.exception.AppException;
import com.example.wallet.exception.AppExceptionHandler;
import com.example.wallet.service.UserService;
import com.example.wallet.service.WalletService;
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

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WalletControllerTest {

    @InjectMocks
    private WalletController walletController;

    @Mock
    private WalletService walletService;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(walletController)
                .setControllerAdvice(new AppExceptionHandler())
                .build();
    }

    @Test
    void shouldDepositAmountToWalletAPIWorkCorrectly() throws Exception {
        Principal principal = () -> "testUser";

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setAmount(0.0);

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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);
        when(walletService.depositAmountToWallet(100.0, wallet.getId())).thenReturn(100.0);

        mockMvc.perform(put("/api/v1/wallet/deposit")
                        .param("amount", "100.0")
                        .principal(principal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.walletId").value(1))
                .andExpect(jsonPath("$.updatedAmount").value(100))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(walletService, times(1)).depositAmountToWallet(100.0, wallet.getId());
    }

    @Test
    void shouldDepositAmountToWalletAPIWorkThrowAnErrorIfTryingToDepositZeroOrLessAmount() throws Exception {
        Principal principal = () -> "testUser";

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setAmount(0.0);

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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);

        doThrow(new AppException(HttpStatus.BAD_REQUEST, "Cannot deposit 0 or less amount"))
                .when(walletService)
                .depositAmountToWallet(-1, 1L);

        mockMvc.perform(put("/api/v1/wallet/deposit")
                        .param("amount", "-1.0")
                        .principal(principal)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Cannot deposit 0 or less amount"));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(walletService, times(1)).depositAmountToWallet(-1.0, wallet.getId());
    }

    @Test
    void shouldDepositAmountToWalletAPIWorkThrowAnErrorIfWalletDoesNotExist() throws Exception {
        Principal principal = () -> "testUser";

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setAmount(0.0);

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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);

        doThrow(new AppException(HttpStatus.NOT_FOUND, "Wallet Not found"))
                .when(walletService)
                .depositAmountToWallet(5, 1L);

        mockMvc.perform(put("/api/v1/wallet/deposit")
                        .param("amount", "5.0")
                        .principal(principal)
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Wallet Not found"));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(walletService, times(1)).depositAmountToWallet(5.0, wallet.getId());
    }

    @Test
    void shouldWithdrawAmountFromAPIWorkCorrectly() throws Exception {
        Principal principal = () -> "testUser";

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setAmount(10.0);

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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);
        when(walletService.withDrawAmountFromWallet(9.0, wallet.getId())).thenReturn(1.0);

        mockMvc.perform(put("/api/v1/wallet/withdraw")
                        .param("amount", "9.0")
                        .principal(principal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.walletId").value(1))
                .andExpect(jsonPath("$.updatedAmount").value(1.0))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(walletService, times(1)).withDrawAmountFromWallet(9.0, wallet.getId());
    }

    @Test
    void shouldWithdrawAmountFromWalletAPIWorkThrowAnErrorIfTryingToWithdrawZeroOrLessAmount() throws Exception {
        Principal principal = () -> "testUser";

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setAmount(0.0);

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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);

        doThrow(new AppException(HttpStatus.BAD_REQUEST, "Cannot withdraw 0 or less amount"))
                .when(walletService)
                .withDrawAmountFromWallet(-1, 1L);

        mockMvc.perform(put("/api/v1/wallet/withdraw")
                        .param("amount", "-1.0")
                        .principal(principal)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Cannot withdraw 0 or less amount"));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(walletService, times(1)).withDrawAmountFromWallet(-1.0, wallet.getId());
    }

    @Test
    void shouldWithdrawAmountFromWalletAPIWorkThrowAnErrorIfWalletDoesNotExist() throws Exception {
        Principal principal = () -> "testUser";

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setAmount(0.0);

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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);

        doThrow(new AppException(HttpStatus.NOT_FOUND, "Wallet Not found"))
                .when(walletService)
                .withDrawAmountFromWallet(5, 1L);

        mockMvc.perform(put("/api/v1/wallet/withdraw")
                        .param("amount", "5.0")
                        .principal(principal)
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Wallet Not found"));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(walletService, times(1)).withDrawAmountFromWallet(5.0, wallet.getId());
    }

    @Test
    void shouldWithdrawAmountFromWalletAPIWorkThrowAnErrorIfTryingToWithdrawMoreThanTheExistingAmount() throws Exception {
        Principal principal = () -> "testUser";

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setAmount(0.0);

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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);

        doThrow(new AppException(HttpStatus.BAD_REQUEST, "Amount exceeded current balance in wallet"))
                .when(walletService)
                .withDrawAmountFromWallet(5, 1L);

        mockMvc.perform(put("/api/v1/wallet/withdraw")
                        .param("amount", "5.0")
                        .principal(principal)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Amount exceeded current balance in wallet"));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(walletService, times(1)).withDrawAmountFromWallet(5.0, wallet.getId());
    }

    @Test
    void shouldFetchCurrentAmountAPIWorkCorrectly() throws Exception {
        Principal principal = () -> "testUser";

        Wallet wallet = new Wallet();
        wallet.setId(1L);

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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);
        when(walletService.getUserWallet(user)).thenReturn(wallet);

        mockMvc.perform(get("/api/v1/wallet/current")
                        .principal(principal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(0.0));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(walletService, times(1)).getUserWallet(user);
    }

}