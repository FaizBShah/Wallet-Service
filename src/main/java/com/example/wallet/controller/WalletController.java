package com.example.wallet.controller;

import com.example.wallet.dto.response.WalletUpdateResponseMessage;
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

    @GetMapping("/current")
    public ResponseEntity<Wallet> fetchCurrentAmount(Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        Wallet wallet = walletService.getUserWallet(user);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/deposit")
    public ResponseEntity<WalletUpdateResponseMessage> depositAmountToWallet(@RequestParam("amount") Double amount, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        double updatedAmount = walletService.depositAmountToWallet(amount, user.getWallet().getId());
        return ResponseEntity.ok(new WalletUpdateResponseMessage(
                true,
                user.getWallet().getId(),
                updatedAmount,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        ));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WalletUpdateResponseMessage> withdrawAmountFromWallet(@RequestParam("amount") Double amount, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        double updatedAmount = walletService.withDrawAmountFromWallet(amount, user.getWallet().getId());
        return ResponseEntity.ok(new WalletUpdateResponseMessage(
                true,
                user.getWallet().getId(),
                updatedAmount,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        ));
    }
}
