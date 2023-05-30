package com.example.wallet.controller;

import com.example.wallet.entity.Currency;
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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);
        when(walletService.depositAmountToWallet(100.0, wallet.getId())).thenReturn(wallet);

        mockMvc.perform(put("/api/v1/wallet/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"amount\": 100.0\n" +
                                "}")
                        .principal(principal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.currency").value(Currency.RUPEE.toString()));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(walletService, times(1)).depositAmountToWallet(100.0, wallet.getId());
    }

    @Test
    void shouldDepositAmountToWalletAPIWorkThrowAnErrorIfTryingToDepositZeroOrLessAmount() throws Exception {
        Principal principal = () -> "testUser";

        Wallet wallet = Wallet.builder()
                .id(1L)
                .amount(0.0)
                .currency(Currency.RUPEE)
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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);

        doThrow(new AppException(HttpStatus.BAD_REQUEST, "Cannot deposit 0 or less amount"))
                .when(walletService)
                .depositAmountToWallet(-1, 1L);

        mockMvc.perform(put("/api/v1/wallet/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"amount\": -1.0\n" +
                                "}")
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

        Wallet wallet = Wallet.builder()
                .id(1L)
                .amount(0.0)
                .currency(Currency.RUPEE)
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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);

        doThrow(new AppException(HttpStatus.NOT_FOUND, "Wallet Not found"))
                .when(walletService)
                .depositAmountToWallet(5, 1L);

        mockMvc.perform(put("/api/v1/wallet/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"amount\": 5.0\n" +
                                "}")
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

        Wallet wallet = Wallet.builder()
                .id(1L)
                .amount(9.0)
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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);
        when(walletService.withDrawAmountFromWallet(9.0, wallet.getId())).thenReturn(wallet);

        mockMvc.perform(put("/api/v1/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"amount\": 9.0\n" +
                                "}")
                        .principal(principal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(9.0))
                .andExpect(jsonPath("$.currency").value(Currency.RUPEE.toString()));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(walletService, times(1)).withDrawAmountFromWallet(9.0, wallet.getId());
    }

    @Test
    void shouldWithdrawAmountFromWalletAPIWorkThrowAnErrorIfTryingToWithdrawZeroOrLessAmount() throws Exception {
        Principal principal = () -> "testUser";

        Wallet wallet = Wallet.builder()
                .id(1L)
                .amount(0.0)
                .currency(Currency.RUPEE)
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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);

        doThrow(new AppException(HttpStatus.BAD_REQUEST, "Cannot withdraw 0 or less amount"))
                .when(walletService)
                .withDrawAmountFromWallet(-1, 1L);

        mockMvc.perform(put("/api/v1/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"amount\": -1.0\n" +
                                "}")
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

        Wallet wallet = Wallet.builder()
                .id(1L)
                .amount(0.0)
                .currency(Currency.RUPEE)
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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);

        doThrow(new AppException(HttpStatus.NOT_FOUND, "Wallet Not found"))
                .when(walletService)
                .withDrawAmountFromWallet(5, 1L);

        mockMvc.perform(put("/api/v1/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"amount\": 5.0\n" +
                                "}")
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

        Wallet wallet = Wallet.builder()
                .id(1L)
                .amount(0.0)
                .currency(Currency.RUPEE)
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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);

        doThrow(new AppException(HttpStatus.BAD_REQUEST, "Amount exceeded current balance in wallet"))
                .when(walletService)
                .withDrawAmountFromWallet(5, 1L);

        mockMvc.perform(put("/api/v1/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"amount\": 5.0\n" +
                                "}")
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
    void shouldFetchWalletAPIWorkCorrectly() throws Exception {
        Principal principal = () -> "testUser";

        Wallet wallet = Wallet.builder()
                .id(1L)
                .amount(0.0)
                .currency(Currency.RUPEE)
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

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);
        when(walletService.getUserWallet(user)).thenReturn(wallet);

        mockMvc.perform(get("/api/v1/wallet")
                        .principal(principal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(0.0));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(walletService, times(1)).getUserWallet(user);
    }

    @Test
    void shouldActivateWalletAPIWorkCorrectly() throws Exception {
        Principal principal = () -> "testUser";

        Wallet wallet = Wallet.builder()
                .id(1L)
                .amount(0.0)
                .currency(Currency.RUPEE)
                .build();

        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("helloworld")
                .enabled(true)
                .locked(false)
                .build();

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);
        when(walletService.activateWallet(user, Currency.RUPEE)).thenReturn(wallet);

        mockMvc.perform(put("/api/v1/wallet/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"currency\": \"RUPEE\"\n" +
                                "}")
                        .principal(principal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(0.0))
                .andExpect(jsonPath("$.currency").value("RUPEE"));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(walletService, times(1)).activateWallet(user, Currency.RUPEE);
    }

    @Test
    void shouldTransferAmountToWalletAPIWorkCorrectly() throws Exception {
        Principal principal = () -> "testUser";

        Wallet wallet = Wallet.builder()
                .id(1L)
                .amount(10.0)
                .currency(Currency.RUPEE)
                .build();

        Wallet expectedWallet = Wallet.builder()
                .id(1L)
                .amount(5.0)
                .currency(Currency.RUPEE)
                .build();

        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .wallet(wallet)
                .password("helloworld")
                .enabled(true)
                .locked(false)
                .build();

        when(userService.loadUserByUsername(principal.getName())).thenReturn(user);
        when(walletService.transferAmountToWallet(5.0, user, 2L)).thenReturn(expectedWallet);

        mockMvc.perform(put("/api/v1/wallet/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"amount\": 5.0,\n" +
                                "\t\"walletId\": 2\n" +
                                "}")
                        .principal(principal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(5.0))
                .andExpect(jsonPath("$.currency").value("RUPEE"));

        verify(userService, times(1)).loadUserByUsername(principal.getName());
        verify(walletService, times(1)).transferAmountToWallet(5.0, user, 2L);
    }

}