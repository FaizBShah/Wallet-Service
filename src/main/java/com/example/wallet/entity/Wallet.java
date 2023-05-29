package com.example.wallet.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        this.isActivated = true;
        this.amount = 0.0;
        this.currency = currency;
    }
}
