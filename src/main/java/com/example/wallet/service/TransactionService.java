package com.example.wallet.service;

import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import com.example.wallet.exception.AppException;
import com.example.wallet.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public void createTransferTransaction(Transaction transaction) {
        if (!transaction.isValidTransferTransaction()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Transaction is not valid transfer transaction");
        }

        transactionRepository.save(transaction);
    }

    public void createDepositTransaction(Transaction transaction) {
        if (!transaction.isValidDepositTransaction()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Transaction is not valid deposit transaction");
        }

        transactionRepository.save(transaction);
    }

    public void createWithdrawTransaction(Transaction transaction) {
        if (!transaction.isValidWithdrawTransaction()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Transaction is not valid withdraw transaction");
        }

        transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions(User user) {
        Wallet wallet = user.getWallet();

        if (!wallet.isActivated()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "User's wallet is not activated yet");
        }

        return transactionRepository.getTransactionsByWalletId(wallet.getId());
    }

}
