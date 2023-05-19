package com.example.wallet.service;

import com.example.wallet.entity.Currency;
import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import com.example.wallet.exception.AppException;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    public Wallet createWallet(User user, Currency currency) {
        if (user.getWallet() != null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "User already has a wallet");
        }

        if (currency == null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid Currency");
        }

        Wallet wallet = Wallet.builder()
                .amount(0.0)
                .currency(currency)
                .build();

        user.setWallet(wallet);

        return userRepository.save(user).getWallet();
    }

    public Double depositAmountToWallet(double amount, Long walletId) {
        if (amount <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot deposit 0 or less amount");
        }

        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Wallet Not found"));

        double newAmount = wallet.getAmount() + amount;
        wallet.setAmount(newAmount);

        return walletRepository.save(wallet).getAmount();
    }

    public Double withDrawAmountFromWallet(double amount, Long walletId) {
        if (amount <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot withdraw 0 or less amount");
        }

        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Wallet Not found"));

        double newAmount = wallet.getAmount() - amount;

        if (newAmount < 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Amount exceeded current balance in wallet");
        }

        wallet.setAmount(newAmount);

        return walletRepository.save(wallet).getAmount();
    }

    public Wallet getUserWallet(User user) {
        if (user == null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid User");
        }

        return user.getWallet();
    }

    @Transactional
    public Wallet transferAmountToWallet(Double amount, User user, Long toWalletId) {
        if (user.getWallet() == null) {
            throw new AppException(HttpStatus.NOT_FOUND, "User does not have a wallet yet");
        }

        if (amount <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot transfer zero or less money");
        }

        Wallet fromWallet = user.getWallet();

        if (fromWallet.getId().equals(toWalletId)) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot transfer money to oneself.");
        }

        if (amount > fromWallet.getAmount()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Trying to transfer more amount than what the wallet holds");
        }

        Wallet toWallet = walletRepository.findById(toWalletId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "The wallet you are trying to transfer does not exist"));

        fromWallet.setAmount(fromWallet.getAmount() - amount);
        toWallet.setAmount(toWallet.getAmount() + fromWallet.getCurrency().convertTo(toWallet.getCurrency(), amount));

        walletRepository.save(toWallet);

        return walletRepository.save(fromWallet);
    }
}
