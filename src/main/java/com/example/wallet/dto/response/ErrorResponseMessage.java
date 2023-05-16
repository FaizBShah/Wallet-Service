package com.example.wallet.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseMessage extends ResponseMessage {

    private final String message;

    public ErrorResponseMessage(boolean success, String message) {
        super(success);
        this.message = message;
    }
}
