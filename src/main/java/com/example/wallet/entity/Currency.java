package com.example.wallet.entity;

public enum Currency {
    DOLLAR(1),
    RUPEE(80),
    YEN(160),
    EURO(0.8);

    private final double currencyToDollarConversionFactor;

    private Currency(double currencyToDollarConversionFactor) {
        this.currencyToDollarConversionFactor = currencyToDollarConversionFactor;
    }

    public double convertTo(Currency currency, double amount) {
        return amount * ((currency.currencyToDollarConversionFactor * 1.0) / (this.currencyToDollarConversionFactor * 1.0));
    }
}
