package com.example.wallet.repository;

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
        Wallet wallet = new Wallet();

        walletRepository.save(wallet);

        assertEquals(wallet, walletRepository.findById(1L).get());
    }
}