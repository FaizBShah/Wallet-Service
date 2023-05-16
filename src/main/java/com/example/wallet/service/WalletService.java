package com.example.wallet.service;

import com.example.wallet.entity.Wallet;
import com.example.wallet.exception.AppException;
import com.example.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public Double depositAmountToWallet(double amount, Long walletId) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Wallet Not found"));

        double newAmount = wallet.getAmount() + amount;
        wallet.setAmount(newAmount);

        return walletRepository.save(wallet).getAmount();
    }

    public Double withDrawAmountFromWallet(double amount, Long walletId) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Wallet Not found"));

        double newAmount = wallet.getAmount() - amount;

        if (newAmount < 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Amount exceeded current balance in wallet");
        }

        wallet.setAmount(newAmount);

        return walletRepository.save(wallet).getAmount();
    }
}
