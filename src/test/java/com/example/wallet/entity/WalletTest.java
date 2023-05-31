package com.example.wallet.entity;

import com.example.wallet.exception.AppException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void shouldActivateWalletWorkCorrectly() {
        assertDoesNotThrow(() -> new Wallet().activate(Currency.RUPEE));
    }

    @Test
    void shouldThrowErrorIfWalletIsAlreadyActivated() {
        Wallet wallet = new Wallet();

        wallet.activate(Currency.RUPEE);

        AppException exception = assertThrows(AppException.class, () -> wallet.activate(Currency.RUPEE));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());
        assertEquals("Wallet is already activated", exception.getMessage());
    }

    @Test
    void shouldThrowErrorIfWalletIsActivatedWithANullCurrency() {
        Wallet wallet = new Wallet();

        AppException exception = assertThrows(AppException.class, () -> wallet.activate(null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cannot activate wallet with a null currency", exception.getMessage());
    }

    @Test
    void shouldDepositMoneyWorkProperly() {
        Wallet wallet = new Wallet();
        Wallet expectedWallet = Wallet.builder()
                .amount(5.0)
                .currency(Currency.RUPEE)
                .isActivated(true)
                .build();

        wallet.activate(Currency.RUPEE);

        wallet.depositMoney(5.0);

        assertEquals(expectedWallet, wallet);
    }

    @Test
    void shouldDepositMoneyThrowAnErrorIfWalletIsNotActivated() {
        Wallet wallet = new Wallet();

        AppException exception = assertThrows(AppException.class, () -> wallet.depositMoney(5.0));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());
        assertEquals("Wallet is not activated yet", exception.getMessage());
    }

    @Test
    void shouldDepositMoneyThrowAnErrorIfZeroOrLessAmountIsDeposited() {
        Wallet wallet = new Wallet();

        wallet.activate(Currency.RUPEE);

        AppException exception = assertThrows(AppException.class, () -> wallet.depositMoney(-1.0));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cannot deposit 0 or less amount", exception.getMessage());
    }

    @Test
    void shouldWithdrawMoneyWorkProperly() {
        Wallet wallet = new Wallet();
        Wallet expectedWallet = Wallet.builder()
                .amount(2.0)
                .currency(Currency.RUPEE)
                .isActivated(true)
                .build();

        wallet.activate(Currency.RUPEE);

        wallet.depositMoney(5.0);
        wallet.withdrawMoney(3.0);

        assertEquals(expectedWallet, wallet);
    }

    @Test
    void shouldWithdrawMoneyThrowAnErrorIfWalletIsNotActivated() {
        Wallet wallet = new Wallet();

        AppException exception = assertThrows(AppException.class, () -> wallet.withdrawMoney(5.0));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());
        assertEquals("Wallet is not activated yet", exception.getMessage());
    }

    @Test
    void shouldWithdrawMoneyThrowAnErrorIfZeroOrLessAmountIsDeposited() {
        Wallet wallet = new Wallet();

        wallet.activate(Currency.RUPEE);

        AppException exception = assertThrows(AppException.class, () -> wallet.withdrawMoney(-1.0));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cannot withdraw 0 or less amount", exception.getMessage());
    }

    @Test
    void shouldWithdrawMoneyThrowAnErrorIfTryingToWithdrawMoneyMoreThanTheCurrentBalance() {
        Wallet wallet = new Wallet();

        wallet.activate(Currency.RUPEE);

        AppException exception = assertThrows(AppException.class, () -> wallet.withdrawMoney(1.0));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());
        assertEquals("Amount exceeded current balance in wallet", exception.getMessage());
    }

    @Test
    void shouldTransferAmountToWalletWorkCorrectly() {
        Wallet fromWallet = Wallet.builder()
                .id(1L)
                .build();
        Wallet toWallet = Wallet.builder()
                .id(2L)
                .build();

        fromWallet.activate(Currency.RUPEE);
        toWallet.activate(Currency.YEN);

        fromWallet.depositMoney(5.0);

        Transaction transaction = fromWallet.transferAmountTo(2.0, toWallet);

        assertNull(transaction.getId());
        assertEquals(1L, transaction.getFromWalletId());
        assertEquals(2.0, transaction.getFromWalletAmount());
        assertEquals(Currency.RUPEE, transaction.getFromWalletCurrency());
        assertEquals(2L, transaction.getToWalletId());
        assertEquals(4.0, transaction.getToWalletAmount());
        assertEquals(Currency.YEN, transaction.getToWalletCurrency());
        assertEquals(TransactionType.TRANSFER, transaction.getTransactionType());
        assertNotNull(transaction.getCreatedAt());
        assertTrue(transaction.getCreatedAt() instanceof LocalDateTime);
    }

    @Test
    void shouldTransferAmountToWalletShouldThrowErrorIfFromWalletIsNotActivated() {
        Wallet fromWallet = Wallet.builder()
                .id(1L)
                .build();
        Wallet toWallet = Wallet.builder()
                .id(2L)
                .build();

        toWallet.activate(Currency.YEN);

        AppException exception = assertThrows(AppException.class, () -> fromWallet.transferAmountTo(2.0, toWallet));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());
        assertEquals("Wallet is not activated yet", exception.getMessage());
    }

    @Test
    void shouldTransferAmountToWalletShouldThrowErrorIfToWalletIsNotActivated() {
        Wallet fromWallet = Wallet.builder()
                .id(1L)
                .build();
        Wallet toWallet = Wallet.builder()
                .id(2L)
                .build();

        fromWallet.activate(Currency.RUPEE);

        fromWallet.depositMoney(5.0);

        AppException exception = assertThrows(AppException.class, () -> fromWallet.transferAmountTo(2.0, toWallet));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());
        assertEquals("The wallet you are trying to transfer is not activated yet", exception.getMessage());
    }

    @Test
    void shouldTransferAmountToWalletShouldThrowErrorIfTryingToTransferAmountToTheSameWallet() {
        Wallet fromWallet = Wallet.builder()
                .id(1L)
                .build();

        fromWallet.activate(Currency.RUPEE);

        fromWallet.depositMoney(5.0);

        AppException exception = assertThrows(AppException.class, () -> fromWallet.transferAmountTo(2.0, fromWallet));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cannot transfer money to oneself", exception.getMessage());
    }

    @Test
    void shouldTransferAmountToWalletShouldThrowErrorIfTryingToTransferMoreAmountThanTheCurrentBalance() {
        Wallet fromWallet = Wallet.builder()
                .id(1L)
                .build();
        Wallet toWallet = Wallet.builder()
                .id(2L)
                .build();

        fromWallet.activate(Currency.RUPEE);
        toWallet.activate(Currency.YEN);

        fromWallet.depositMoney(5.0);

        AppException exception = assertThrows(AppException.class, () -> fromWallet.transferAmountTo(6.0, toWallet));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());
        assertEquals("Cannot transfer more than your current balance", exception.getMessage());
    }

}