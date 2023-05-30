package com.example.wallet.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void shouldIsValidTransferTransactionWorkProperly() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(2L)
                .toWalletAmount(2.0)
                .toWalletCurrency(Currency.YEN)
                .transactionType(TransactionType.TRANSFER)
                .createdAt(LocalDateTime.now())
                .build();

        assertTrue(transaction.isValidTransferTransaction());
    }

    @Test
    void shouldIsValidTransferTransactionReturnFalseIfTryingToTransferAmountToSameWallet() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(1.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.TRANSFER)
                .createdAt(LocalDateTime.now())
                .build();

        assertFalse(transaction.isValidTransferTransaction());
    }

    @Test
    void shouldIsValidTransferTransactionReturnFalseIfTryingToTransferNegativeAmount() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(-1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(2L)
                .toWalletAmount(2.0)
                .toWalletCurrency(Currency.YEN)
                .transactionType(TransactionType.TRANSFER)
                .createdAt(LocalDateTime.now())
                .build();

        assertFalse(transaction.isValidTransferTransaction());
    }

    @Test
    void shouldIsValidTransferTransactionReturnFalseIfTryingToTransferIncorrectConversionAmount() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(2L)
                .toWalletAmount(1.5)
                .toWalletCurrency(Currency.YEN)
                .transactionType(TransactionType.TRANSFER)
                .createdAt(LocalDateTime.now())
                .build();

        assertFalse(transaction.isValidTransferTransaction());
    }

    @Test
    void shouldIsValidTransferTransactionReturnFalseIfTransactionTypeIsNotTransfer() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(2L)
                .toWalletAmount(1.5)
                .toWalletCurrency(Currency.YEN)
                .transactionType(TransactionType.DEPOSIT)
                .createdAt(LocalDateTime.now())
                .build();

        assertFalse(transaction.isValidTransferTransaction());
    }

    @Test
    void shouldIsValidTransferTransactionReturnFalseIfCreatedAtIsNull() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(2L)
                .toWalletAmount(1.5)
                .toWalletCurrency(Currency.YEN)
                .transactionType(TransactionType.TRANSFER)
                .build();

        assertFalse(transaction.isValidTransferTransaction());
    }

    @Test
    void shouldIsValidDepositTransactionWorkProperly() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(1.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.DEPOSIT)
                .createdAt(LocalDateTime.now())
                .build();

        assertTrue(transaction.isValidDepositTransaction());
    }

    @Test
    void shouldIsValidDepositTransactionReturnFalseIfIdsAreDifferent() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(2L)
                .toWalletAmount(1.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.DEPOSIT)
                .createdAt(LocalDateTime.now())
                .build();

        assertFalse(transaction.isValidDepositTransaction());
    }

    @Test
    void shouldIsValidDepositTransactionReturnFalseIfAmountsAreDifferent() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(2.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.DEPOSIT)
                .createdAt(LocalDateTime.now())
                .build();

        assertFalse(transaction.isValidDepositTransaction());
    }

    @Test
    void shouldIsValidDepositTransactionReturnFalseIfCurrenciesAreDifferent() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(1.0)
                .toWalletCurrency(Currency.YEN)
                .transactionType(TransactionType.DEPOSIT)
                .createdAt(LocalDateTime.now())
                .build();

        assertFalse(transaction.isValidDepositTransaction());
    }

    @Test
    void shouldIsValidDepositTransactionReturnFalseIfTransactionTypeIsNotDeposit() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(1.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.WITHDRAW)
                .createdAt(LocalDateTime.now())
                .build();

        assertFalse(transaction.isValidDepositTransaction());
    }

    @Test
    void shouldIsValidDepositTransactionReturnFalseIfCreatedAtIsNull() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(1.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.DEPOSIT)
                .build();

        assertFalse(transaction.isValidDepositTransaction());
    }

    @Test
    void shouldIsValidWithdrawTransactionWorkProperly() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(1.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.WITHDRAW)
                .createdAt(LocalDateTime.now())
                .build();

        assertTrue(transaction.isValidWithdrawTransaction());
    }

    @Test
    void shouldIsValidWithdrawTransactionReturnFalseIfIdsAreDifferent() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(2L)
                .toWalletAmount(1.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.WITHDRAW)
                .createdAt(LocalDateTime.now())
                .build();

        assertFalse(transaction.isValidWithdrawTransaction());
    }

    @Test
    void shouldIsValidWithdrawTransactionReturnFalseIfAmountsAreDifferent() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(2.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.WITHDRAW)
                .createdAt(LocalDateTime.now())
                .build();

        assertFalse(transaction.isValidWithdrawTransaction());
    }

    @Test
    void shouldIsValidWithdrawTransactionReturnFalseIfCurrenciesAreDifferent() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(1.0)
                .toWalletCurrency(Currency.YEN)
                .transactionType(TransactionType.WITHDRAW)
                .createdAt(LocalDateTime.now())
                .build();

        assertFalse(transaction.isValidWithdrawTransaction());
    }

    @Test
    void shouldIsValidWithdrawTransactionReturnFalseIfTransactionTypeIsNotWithdraw() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(1.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.DEPOSIT)
                .createdAt(LocalDateTime.now())
                .build();

        assertFalse(transaction.isValidWithdrawTransaction());
    }

    @Test
    void shouldIsValidWithdrawTransactionReturnFalseIfCreatedAtIsNull() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(1.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(1L)
                .toWalletAmount(1.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.WITHDRAW)
                .build();

        assertFalse(transaction.isValidWithdrawTransaction());
    }

}