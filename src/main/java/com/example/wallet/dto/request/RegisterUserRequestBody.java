package com.example.wallet.dto.request;

import lombok.ToString;

@ToString
public record RegisterUserRequestBody(String firstName, String lastName, String email, String password) {
}
