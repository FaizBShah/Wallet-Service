package com.example.wallet.dto.request;

public record RegisterUserRequestBody(String firstName, String lastName, String email, String password) {
}
