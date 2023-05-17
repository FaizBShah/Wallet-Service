package com.example.wallet.repository;

import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    void shouldSaveWorkCorrectly() {
        Wallet wallet = new Wallet();
        User user = User.builder()
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("ghjhjkhkjhkjhjk")
                .wallet(wallet)
                .enabled(true)
                .locked(false)
                .build();

        userRepository.save(user);

        User savedUser = userRepository.findById(1L).get();

        assertEquals(user, savedUser);
        assertEquals(wallet, savedUser.getWallet());
    }

    @Test
    void shouldFindByEmailWorkCorrectly() {
        Wallet wallet = new Wallet();
        User user = User.builder()
                .firstName("Faiz")
                .lastName("Shah")
                .email("faizbshah2001@gmail.com")
                .password("ghjhjkhkjhkjhjk")
                .wallet(wallet)
                .enabled(true)
                .locked(false)
                .build();

        userRepository.save(user);

        User savedUser = userRepository.findByEmail("faizbshah2001@gmail.com").get();

        assertEquals(user, savedUser);
        assertEquals(wallet, savedUser.getWallet());
    }
}