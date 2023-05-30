package com.example.wallet.repository;

import com.example.wallet.entity.Currency;
import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void shouldTransactionSaveWorkCorrectly() {
        Transaction transaction = Transaction.builder()
                .fromWalletId(1L)
                .fromWalletAmount(5.0)
                .fromWalletCurrency(Currency.RUPEE)
                .toWalletId(2L)
                .toWalletAmount(5.0)
                .toWalletCurrency(Currency.RUPEE)
                .transactionType(TransactionType.TRANSFER)
                .createdAt(LocalDateTime.of(2023, 1, 1, 10, 10))
                .build();

        transactionRepository.save(transaction);

        assertEquals(transaction, transactionRepository.findById(1L).get());
    }
}