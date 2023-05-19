package com.example.wallet.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyTest {

    @Test
    void shouldConvertToWorkCorrectly() {
        assertEquals(2, Currency.RUPEE.convertTo(Currency.YEN, 1));
        assertEquals(0.8, Currency.DOLLAR.convertTo(Currency.EURO, 1));
    }

}