package com.example.wallet.service;

import com.example.wallet.entity.Currency;
import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import com.example.wallet.exception.AppException;
import com.example.wallet.repository.TransactionRepository;
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

    @Autowired
    private TransactionService transactionService;

    public Wallet activateWallet(User user, Currency currency) {
        Wallet wallet = user.getWallet();

        if (wallet.isActivated()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "User already has a wallet");
        }

        if (currency == null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid Currency");
        }

        wallet.activate(currency);

        return walletRepository.save(wallet);
    }

    @Transactional
    public Double depositAmountToWallet(double amount, Long walletId) {
        if (amount <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot deposit 0 or less amount");
        }

        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Wallet Not found"));
        Transaction depositTransaction = wallet.depositMoney(amount);

        transactionService.createDepositTransaction(depositTransaction);

        return walletRepository.save(wallet).getAmount();
    }

    @Transactional
    public Double withDrawAmountFromWallet(double amount, Long walletId) {
        if (amount <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot withdraw 0 or less amount");
        }

        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Wallet Not found"));
        Transaction withdrawTransaction = wallet.withdrawMoney(amount);

        transactionService.createWithdrawTransaction(withdrawTransaction);

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
        Wallet fromWallet = user.getWallet();
        Wallet toWallet = walletRepository.findById(toWalletId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "The wallet you are trying to transfer does not exist"));

        Transaction transaction = fromWallet.transferAmountTo(amount, toWallet);
        transactionService.createTransferTransaction(transaction);

        walletRepository.save(toWallet);

        return walletRepository.save(fromWallet);
    }
}
