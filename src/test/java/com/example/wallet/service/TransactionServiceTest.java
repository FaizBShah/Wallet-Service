package com.example.wallet.service;

import com.example.wallet.entity.*;
import com.example.wallet.exception.AppException;
import com.example.wallet.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateTransferTransactionWorkCorrectly() {
        Transaction transaction = Transaction.builder()
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

        when(transactionRepository.save(transaction)).thenReturn(transaction);

        transactionService.createTransferTransaction(transaction);

        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void shouldCreateTransferTransactionThrowAnErrorIfItsAnInvalidTransferTransaction() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .fromWalletId(1L)
                .fromWalletAmount(5.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(5.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.TRANSFER)
                .createdAt(LocalDateTime.of(2023, 1, 1, 10, 10))
                .build();

        AppException exception = assertThrows(AppException.class, () -> transactionService.createTransferTransaction(transaction));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Transaction is not valid transfer transaction", exception.getMessage());

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldCreateDepositTransactionWorkCorrectly() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .fromWalletId(1L)
                .fromWalletAmount(5.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(5.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.DEPOSIT)
                .createdAt(LocalDateTime.of(2023, 1, 1, 10, 10))
                .build();

        when(transactionRepository.save(transaction)).thenReturn(transaction);

        transactionService.createDepositTransaction(transaction);

        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void shouldCreateDepositTransactionThrowAnErrorIfItsAnInvalidTransferTransaction() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .fromWalletId(1L)
                .fromWalletAmount(5.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(5.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.TRANSFER)
                .createdAt(LocalDateTime.of(2023, 1, 1, 10, 10))
                .build();

        AppException exception = assertThrows(AppException.class, () -> transactionService.createDepositTransaction(transaction));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Transaction is not valid deposit transaction", exception.getMessage());

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldCreateWithdrawTransactionWorkCorrectly() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .fromWalletId(1L)
                .fromWalletAmount(5.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(5.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.WITHDRAW)
                .createdAt(LocalDateTime.of(2023, 1, 1, 10, 10))
                .build();

        when(transactionRepository.save(transaction)).thenReturn(transaction);

        transactionService.createWithdrawTransaction(transaction);

        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void shouldCreateWithdrawTransactionThrowAnErrorIfItsAnInvalidTransferTransaction() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .fromWalletId(1L)
                .fromWalletAmount(5.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(5.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.TRANSFER)
                .createdAt(LocalDateTime.of(2023, 1, 1, 10, 10))
                .build();

        AppException exception = assertThrows(AppException.class, () -> transactionService.createWithdrawTransaction(transaction));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Transaction is not valid withdraw transaction", exception.getMessage());

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldGetAllTransactionsWorkCorrectly() {
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

        user.getWallet().activate(Currency.RUPEE);

        Transaction transaction1 = user.getWallet().depositMoney(5.0);
        Transaction transaction2 = user.getWallet().depositMoney(6.0);

        when(transactionRepository.getTransactionsByWalletId(1L)).thenReturn(List.of(transaction1, transaction2));

        List<Transaction> transactions = transactionService.getAllTransactions(user);

        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        assertEquals(transaction1, transactions.get(0));
        assertEquals(transaction2, transactions.get(1));

        verify(transactionRepository, times(1)).getTransactionsByWalletId(1L);
    }

    @Test
    void shouldGetAllTransactionsThrowErrorIfWalletIsNotActivated() {
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

        AppException exception = assertThrows(AppException.class, () -> transactionService.getAllTransactions(user));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User's wallet is not activated yet", exception.getMessage());

        verify(transactionRepository, never()).getTransactionsByWalletId(any());
    }

}