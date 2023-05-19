package com.example.wallet.repository;

import com.example.wallet.entity.Currency;
import com.example.wallet.entity.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Test
    public void shouldSaveWorkCorrectly() {
        Wallet wallet = Wallet.builder()
                .amount(0.0)
                .currency(Currency.RUPEE)
                .build();

        walletRepository.save(wallet);

        assertEquals(wallet, walletRepository.findById(1L).get());
    }
}