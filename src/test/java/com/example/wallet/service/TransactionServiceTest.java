package com.example.wallet.service;

import com.example.wallet.entity.Currency;
import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.TransactionType;
import com.example.wallet.exception.AppException;
import com.example.wallet.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
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

}