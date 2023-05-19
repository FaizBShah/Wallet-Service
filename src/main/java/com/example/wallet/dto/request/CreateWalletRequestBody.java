package com.example.wallet.dto.request;

import com.example.wallet.entity.Currency;

public record CreateWalletRequestBody(Currency currency) {
}
