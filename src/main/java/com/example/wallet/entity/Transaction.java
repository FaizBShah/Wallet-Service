package com.example.wallet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @SequenceGenerator(
            name = "transaction_sequence",
            sequenceName = "transaction_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "transaction_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private Long fromWalletId;

    @Column(nullable = false)
    private Double fromWalletAmount;

    @Column(nullable = false)
    private Currency fromWalletCurrency;

    @Column(nullable = false)
    private Long toWalletId;

    @Column(nullable = false)
    private Double toWalletAmount;

    @Column(nullable = false)
    private Currency toWalletCurrency;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public boolean isValidTransferTransaction() {
        return fromWalletId != null &&
                fromWalletAmount > 0 &&
                fromWalletCurrency != null &&
                toWalletId != null &&
                toWalletAmount > 0 &&
                toWalletCurrency != null &&
                !fromWalletId.equals(toWalletId) &&
                transactionType == TransactionType.TRANSFER;
    }

    public boolean isValidDepositTransaction() {
        return fromWalletId != null &&
                fromWalletAmount > 0 &&
                fromWalletCurrency != null &&
                fromWalletId.equals(toWalletId) &&
                fromWalletAmount.equals(toWalletAmount) &&
                toWalletCurrency == fromWalletCurrency &&
                transactionType == TransactionType.DEPOSIT;
    }

    public boolean isValidWithdrawTransaction() {
        return fromWalletId != null &&
                fromWalletAmount > 0 &&
                fromWalletCurrency != null &&
                fromWalletId.equals(toWalletId) &&
                fromWalletAmount.equals(toWalletAmount) &&
                toWalletCurrency == fromWalletCurrency &&
                transactionType == TransactionType.WITHDRAW;
    }
}
