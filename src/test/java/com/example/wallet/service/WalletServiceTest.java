package com.example.wallet.service;

import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import com.example.wallet.exception.AppException;
import com.example.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    private Wallet wallet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setAmount(5.0);
    }

    @Test
    void shouldDepositAmountToWalletWorkCorrectly() {
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Double updatedAmount = walletService.depositAmountToWallet(6.5, 1L);

        assertEquals(11.5, updatedAmount);

        verify(walletRepository, times(1)).findById(wallet.getId());
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void shouldDepositAmountToWalletThrowAnErrorIfAmountDepositedIsLessThanZero() {
        AppException exception = assertThrows(AppException.class, () -> walletService.depositAmountToWallet(-1, 1L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cannot deposit 0 or less amount", exception.getMessage());

        verify(walletRepository, never()).findById(wallet.getId());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void shouldDepositAmountToWalletThrowAnErrorIfWalletDoesNotExist() {
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> walletService.depositAmountToWallet(6.5, 1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Wallet Not found", exception.getMessage());

        verify(walletRepository, times(1)).findById(wallet.getId());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void shouldWithdrawAmountFromWalletWorkCorrectly() {
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Double updatedAmount = walletService.withDrawAmountFromWallet(3.0, 1L);

        assertEquals(2.0, updatedAmount);

        verify(walletRepository, times(1)).findById(wallet.getId());
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void shouldWithdrawAmountFromWalletThrowAnErrorIfAmountWithdrawnIsLessThanZero() {
        AppException exception = assertThrows(AppException.class, () -> walletService.withDrawAmountFromWallet(-1, 1L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cannot withdraw 0 or less amount", exception.getMessage());

        verify(walletRepository, never()).findById(wallet.getId());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void shouldWithdrawAmountFromWalletThrowAnErrorIfWalletDoesNotExist() {
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> walletService.withDrawAmountFromWallet(3.0, 1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Wallet Not found", exception.getMessage());

        verify(walletRepository, times(1)).findById(wallet.getId());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void shouldWithdrawAmountFromWalletThrowAnErrorIfTryingToWithdrawAmountMoreThanTheCurrentBalance() {
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

        AppException exception = assertThrows(AppException.class, () -> walletService.withDrawAmountFromWallet(6.5, 1L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Amount exceeded current balance in wallet", exception.getMessage());

        verify(walletRepository, times(1)).findById(wallet.getId());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void shouldGetUserWalletWorkProperly() {
        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("hjhjkjjkh")
                .wallet(wallet)
                .enabled(true)
                .locked(false)
                .build();

        Wallet userWallet = walletService.getUserWallet(user);

        assertNotNull(userWallet);
        assertEquals(wallet, userWallet);
    }

    @Test
    void shouldGetUserWalletThrowAnErrorIfUserIsNull() {
        AppException exception = assertThrows(AppException.class, () -> walletService.getUserWallet(null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid User", exception.getMessage());
    }

}