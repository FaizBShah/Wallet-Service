package com.example.wallet.controller;

import com.example.wallet.dto.request.RegisterUserRequestBody;
import com.example.wallet.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequestBody requestBody) {
        authService.registerUser(
                requestBody.firstName(),
                requestBody.lastName(),
                requestBody.email(),
                requestBody.password()
        );

        return ResponseEntity.ok(true);
    }
}
