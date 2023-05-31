package com.example.wallet.controller;

import com.example.wallet.dto.request.LoginUserRequestBody;
import com.example.wallet.dto.request.RegisterUserRequestBody;
import com.example.wallet.dto.response.LoginSuccessResponseMessage;
import com.example.wallet.dto.response.ResponseMessage;
import com.example.wallet.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ResponseMessage> registerUser(@RequestBody RegisterUserRequestBody requestBody) {
        authService.registerUser(
                requestBody.firstName(),
                requestBody.lastName(),
                requestBody.email(),
                requestBody.password()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(true));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginSuccessResponseMessage> loginUser(@RequestBody LoginUserRequestBody requestBody, HttpServletRequest request) {
        String token = authService.loginUser(requestBody.email(), requestBody.password(), request);
        return ResponseEntity.ok(new LoginSuccessResponseMessage(true, token));
    }
}
