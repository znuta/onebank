package com.onebank.onebank.payment.dto.enums;

public enum CurrencyType {
    NGN("NGN"),
    USD("USD");

    private final String currency;

    CurrencyType(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return this.currency;
    }
}
