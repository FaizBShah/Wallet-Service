package com.example.wallet.entity;

import com.example.wallet.exception.AppException;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

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
            throw new AppException(HttpStatus.BAD_REQUEST, "Wallet is already activated");
        }

        this.isActivated = true;
        this.amount = 0.0;
        this.currency = currency;
    }

    public Double depositMoney(Double amount) {
        if (!isActivated()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Wallet is not activated yet");
        }

        if (amount <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot deposit 0 or less amount");
        }

        return this.amount += amount;
    }

    public Double withdrawMoney(Double amount) {
        if (!isActivated()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Wallet is not activated yet");
        }

        if (amount <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot deposit 0 or less amount");
        }

        if (amount > this.amount) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Amount exceeded current balance in wallet");
        }

        return this.amount -= amount;
    }

    public Double[] transferAmountTo(Double amount, Wallet toWallet) {
        if (!isActivated()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Wallet is not activated yet");
        }

        if (!toWallet.isActivated()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "The wallet you are trying to transfer is not activated yet");
        }

        if (id == toWallet.id) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot transfer money to oneself.");
        }

        if (amount > this.amount) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot transfer more than your current balance");
        }

        Double currFromAmount = this.withdrawMoney(amount);
        Double currToAmount = this.depositMoney(this.currency.convertTo(toWallet.getCurrency(), amount));

        return new Double[] { currFromAmount, currToAmount };
    }
}
