package com.example.wallet.entity;

import com.example.wallet.exception.AppException;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @SequenceGenerator(
            name = "wallet_sequence",
            sequenceName = "wallet_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "wallet_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private Currency currency = null;

    @Column(nullable = false)
    private boolean isActivated = false;

    @JsonBackReference
    @OneToOne(mappedBy = "wallet")
    private User user;

    public void activate(Currency currency) {
        if (isActivated()) {
            throw new AppException(HttpStatus.UNPROCESSABLE_ENTITY, "Wallet is already activated");
        }

        if (currency == null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot activate wallet with a null currency");
        }

        this.isActivated = true;
        this.amount = 0.0;
        this.currency = currency;
    }

    public Transaction depositMoney(Double amount) {
        if (!isActivated()) {
            throw new AppException(HttpStatus.UNPROCESSABLE_ENTITY, "Wallet is not activated yet");
        }

        if (amount <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot deposit 0 or less amount");
        }

        this.amount += amount;

        return Transaction.builder()
                .fromWalletId(id)
                .fromWalletAmount(amount)
                .fromWalletCurrency(currency)
                .toWalletId(id)
                .toWalletAmount(amount)
                .toWalletCurrency(currency)
                .transactionType(TransactionType.DEPOSIT)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Transaction withdrawMoney(Double amount) {
        if (!isActivated()) {
            throw new AppException(HttpStatus.UNPROCESSABLE_ENTITY, "Wallet is not activated yet");
        }

        if (amount <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot withdraw 0 or less amount");
        }

        if (amount > this.amount) {
            throw new AppException(HttpStatus.UNPROCESSABLE_ENTITY, "Amount exceeded current balance in wallet");
        }

        this.amount -= amount;

        return Transaction.builder()
                .fromWalletId(id)
                .fromWalletAmount(amount)
                .fromWalletCurrency(currency)
                .toWalletId(id)
                .toWalletAmount(amount)
                .toWalletCurrency(currency)
                .transactionType(TransactionType.WITHDRAW)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Transaction transferAmountTo(Double amount, Wallet toWallet) {
        if (!isActivated()) {
            throw new AppException(HttpStatus.UNPROCESSABLE_ENTITY, "Wallet is not activated yet");
        }

        if (!toWallet.isActivated()) {
            throw new AppException(HttpStatus.UNPROCESSABLE_ENTITY, "The wallet you are trying to transfer is not activated yet");
        }

        if (id.equals(toWallet.id)) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot transfer money to oneself");
        }

        if (amount > this.amount) {
            throw new AppException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot transfer more than your current balance");
        }

        this.withdrawMoney(amount);
        toWallet.depositMoney(this.currency.convertTo(toWallet.getCurrency(), amount));

        return Transaction.builder()
                .fromWalletId(id)
                .fromWalletAmount(amount)
                .fromWalletCurrency(currency)
                .toWalletId(toWallet.id)
                .toWalletAmount(currency.convertTo(toWallet.currency, amount))
                .toWalletCurrency(toWallet.currency)
                .transactionType(TransactionType.TRANSFER)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
