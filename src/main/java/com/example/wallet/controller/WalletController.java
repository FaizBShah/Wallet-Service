package com.example.wallet.controller;

import com.example.wallet.dto.request.CreateWalletRequestBody;
import com.example.wallet.dto.request.TransferAmountRequestBody;
import com.example.wallet.dto.request.WalletUpdateRequestBody;
import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import com.example.wallet.service.UserService;
import com.example.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Wallet> fetchWallet(Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        Wallet wallet = walletService.getUserWallet(user);
        return ResponseEntity.ok(wallet);
    }

    @PutMapping("/activate")
    public ResponseEntity<Wallet> activateWallet(@RequestBody CreateWalletRequestBody requestBody, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        Wallet wallet = walletService.activateWallet(user, requestBody.currency());
        return ResponseEntity.ok(wallet);
    }

    @PutMapping("/deposit")
    public ResponseEntity<Wallet> depositAmountToWallet(@RequestBody WalletUpdateRequestBody requestBody, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        Wallet wallet = walletService.depositAmountToWallet(requestBody.amount(), user.getWallet().getId());
        return ResponseEntity.ok(wallet);
    }

    @PutMapping("/withdraw")
    public ResponseEntity<Wallet> withdrawAmountFromWallet(@RequestBody WalletUpdateRequestBody requestBody, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        Wallet wallet = walletService.withDrawAmountFromWallet(requestBody.amount(), user.getWallet().getId());
        return ResponseEntity.ok(wallet);
    }

    @PutMapping("/transfer")
    public ResponseEntity<Wallet> transferAmountToWallet(@RequestBody TransferAmountRequestBody requestBody, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        Wallet wallet = walletService.transferAmountToWallet(requestBody.amount(), user, requestBody.walletId());
        return ResponseEntity.ok(wallet);
    }
}
