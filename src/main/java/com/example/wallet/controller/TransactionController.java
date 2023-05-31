package com.example.wallet.controller;

import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.User;
import com.example.wallet.service.TransactionService;
import com.example.wallet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        List<Transaction> transactions = transactionService.getAllTransactions(user);
        return ResponseEntity.ok(transactions);
    }
}
