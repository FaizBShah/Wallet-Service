package com.example.wallet.service;

import com.example.wallet.entity.Wallet;
import com.example.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public Double depositAmountToWallet(double amount, Long walletId) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new IllegalStateException("Wallet Not found"));

        double newAmount = wallet.getAmount() + amount;
        wallet.setAmount(newAmount);

        return walletRepository.save(wallet).getAmount();
    }

    public Double withDrawAmountFromWallet(double amount, Long walletId) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new IllegalStateException("Wallet Not found"));

        double newAmount = wallet.getAmount() - amount;

        if (newAmount < 0) {
            throw new IllegalStateException("Amount exceeded current balance in wallet");
        }

        wallet.setAmount(newAmount);

        return walletRepository.save(wallet).getAmount();
    }
}
