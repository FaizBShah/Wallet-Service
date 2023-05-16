package com.example.wallet.dto.request;

import lombok.ToString;

@ToString
public record LoginUserRequestBody(String email, String password) {
}
