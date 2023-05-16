package com.example.wallet.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "wallet")
@Data
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

    public Wallet() {
        this.amount = 0.0;
    }
}
