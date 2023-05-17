package com.example.wallet.dto.response;

public class WalletUpdateResponseMessage extends ResponseMessage {

    private final Long walletId;
    private final Double updatedAmount;
    private final String firstName;
    private final String lastName;
    private final String email;

    public WalletUpdateResponseMessage(boolean success, Long walletId, Double updatedAmount, String firstName, String lastName, String email) {
        super(success);
        this.walletId = walletId;
        this.updatedAmount = updatedAmount;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
