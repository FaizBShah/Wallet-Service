package com.example.wallet.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginSuccessResponseMessage extends ResponseMessage {

    private final String token;

    public LoginSuccessResponseMessage(boolean success, String token) {
        super(success);
        this.token = token;
    }
}
