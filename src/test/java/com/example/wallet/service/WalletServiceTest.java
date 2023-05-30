package com.example.wallet.service;

import com.example.wallet.entity.Currency;
import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import com.example.wallet.exception.AppException;
import com.example.wallet.repository.UserRepository;
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

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionService transactionService;

    private Wallet wallet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        wallet = Wallet.builder()
                .id(1L)
                .amount(5.0)
                .currency(Currency.RUPEE)
                .isActivated(true)
                .build();
    }

    @Test
    void shouldActivateWalletWorkCorrectly() {
        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("hjhjkjjkh")
                .wallet(Wallet.builder().id(1L).build())
                .enabled(true)
                .locked(false)
                .build();

        Wallet expectedWallet = Wallet.builder()
                        .id(1L)
                        .amount(0.0)
                        .currency(Currency.RUPEE)
                        .isActivated(true)
                        .build();

        when(walletRepository.save(any(Wallet.class))).thenReturn(expectedWallet);

        Wallet savedWallet = walletService.activateWallet(user, Currency.RUPEE);

        assertEquals(expectedWallet, savedWallet);
        assertEquals(expectedWallet, user.getWallet());

        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void shouldActivateWalletThrowAnErrorIfWalletIsAlreadyActivated() {
        Wallet wallet = Wallet.builder()
                        .id(1L)
                        .build();
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

        wallet.activate(Currency.RUPEE);

        AppException exception = assertThrows(AppException.class, () -> walletService.activateWallet(user, Currency.RUPEE));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User already has a wallet", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldActivateWalletThrowErrorIfUserSendsANullCurrency() {
        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("hjhjkjjkh")
                .wallet(new Wallet())
                .enabled(true)
                .locked(false)
                .build();

        AppException exception = assertThrows(AppException.class, () -> walletService.activateWallet(user, null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid Currency", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldDepositAmountToWalletWorkCorrectly() {
        Wallet expectedWallet = Wallet.builder()
                .id(1L)
                .amount(11.5)
                .currency(Currency.RUPEE)
                .isActivated(true)
                .build();

        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        doAnswer((t) -> { return null; }).when(transactionService).createDepositTransaction(any(Transaction.class));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet resultWallet = walletService.depositAmountToWallet(6.5, 1L);

        assertEquals(expectedWallet, resultWallet);

        verify(walletRepository, times(1)).findById(wallet.getId());
        verify(transactionService, times(1)).createDepositTransaction(any(Transaction.class));
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
        Wallet expectedWallet = Wallet.builder()
                .id(1L)
                .amount(2.0)
                .currency(Currency.RUPEE)
                .isActivated(true)
                .build();

        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        doAnswer((t) -> { return null; }).when(transactionService).createWithdrawTransaction(any(Transaction.class));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet resultWallet = walletService.withDrawAmountFromWallet(3.0, 1L);

        assertEquals(expectedWallet, resultWallet);

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

    @Test
    void shouldTransferAmountToWalletWorkCorrectly() {
        Wallet fromWallet = Wallet.builder()
                .id(1L)
                .amount(10.00)
                .currency(Currency.RUPEE)
                .isActivated(true)
                .build();

        Wallet toWallet = Wallet.builder()
                .id(2L)
                .amount(10.00)
                .currency(Currency.YEN)
                .isActivated(true)
                .build();

        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("hjhjkjjkh")
                .wallet(fromWallet)
                .enabled(true)
                .locked(false)
                .build();

        when(walletRepository.findById(2L)).thenReturn(Optional.of(toWallet));
        doAnswer((t) -> { return null; }).when(transactionService).createTransferTransaction(any(Transaction.class));
        when(walletRepository.save(fromWallet)).thenReturn(fromWallet);
        when(walletRepository.save(toWallet)).thenReturn(toWallet);

        Wallet resultWallet = walletService.transferAmountToWallet(5.0, user, toWallet.getId());

        assertEquals(fromWallet.getId(), resultWallet.getId());
        assertEquals(fromWallet.getCurrency(), resultWallet.getCurrency());
        assertEquals(5.0, resultWallet.getAmount());
        assertEquals(20.0, toWallet.getAmount());

        verify(walletRepository, times(1)).findById(2L);
        verify(transactionService, times(1)).createTransferTransaction(any(Transaction.class));
        verify(walletRepository, times(1)).save(fromWallet);
        verify(walletRepository, times(1)).save(toWallet);
    }

    @Test
    void shouldTransferAmountToWalletThrowErrorIfUserWalletIsNotActiveYet() {
        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("hjhjkjjkh")
                .wallet(new Wallet())
                .enabled(true)
                .locked(false)
                .build();

        AppException exception = assertThrows(AppException.class, () -> walletService.transferAmountToWallet(5.0, user, 2L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User's wallet is not activated yet", exception.getMessage());

        verify(walletRepository, never()).findById(2L);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void shouldTransferAmountToWalletThrowErrorIfUserTransfersNegativeAmount() {
        Wallet fromWallet = Wallet.builder()
                .id(1L)
                .amount(10.00)
                .currency(Currency.RUPEE)
                .isActivated(true)
                .build();

        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("hjhjkjjkh")
                .wallet(fromWallet)
                .enabled(true)
                .locked(false)
                .build();

        AppException exception = assertThrows(AppException.class, () -> walletService.transferAmountToWallet(-1.0, user, 2L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cannot transfer zero or less money", exception.getMessage());

        verify(walletRepository, never()).findById(2L);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void shouldTransferAmountToWalletThrowErrorIfUserTriesToTransferMoneyToOneself() {
        Wallet fromWallet = Wallet.builder()
                .id(1L)
                .amount(10.00)
                .currency(Currency.RUPEE)
                .isActivated(true)
                .build();

        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("hjhjkjjkh")
                .wallet(fromWallet)
                .enabled(true)
                .locked(false)
                .build();

        when(walletRepository.findById(1L)).thenReturn(Optional.of(fromWallet));

        AppException exception = assertThrows(AppException.class, () -> walletService.transferAmountToWallet(5.0, user, 1L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cannot transfer money to oneself", exception.getMessage());

        verify(walletRepository, times(1)).findById(1L);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void shouldTransferAmountToWalletThrowErrorIfUserTriesToTransferMoneyToANonActivatedWallet() {
        Wallet fromWallet = Wallet.builder()
                .id(1L)
                .amount(10.00)
                .currency(Currency.RUPEE)
                .isActivated(true)
                .build();

        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("hjhjkjjkh")
                .wallet(fromWallet)
                .enabled(true)
                .locked(false)
                .build();

        when(walletRepository.findById(2L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> walletService.transferAmountToWallet(5.0, user, 2L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("The wallet you are trying to transfer does not exist", exception.getMessage());

        verify(walletRepository, times(1)).findById(2L);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void shouldTransferAmountToWalletThrowErrorIfUserTransfersAmountMoreThanTheirCurrentAmount() {
        Wallet fromWallet = Wallet.builder()
                .id(1L)
                .amount(10.00)
                .currency(Currency.RUPEE)
                .isActivated(true)
                .build();

        Wallet toWallet = Wallet.builder()
                .id(2L)
                .amount(10.00)
                .currency(Currency.YEN)
                .isActivated(true)
                .build();

        User user = User.builder()
                .id(1L)
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("hjhjkjjkh")
                .wallet(fromWallet)
                .enabled(true)
                .locked(false)
                .build();

        when(walletRepository.findById(2L)).thenReturn(Optional.of(toWallet));

        AppException exception = assertThrows(AppException.class, () -> walletService.transferAmountToWallet(12.0, user, 2L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cannot transfer more than your current balance", exception.getMessage());

        verify(walletRepository, times(1)).findById(2L);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

}