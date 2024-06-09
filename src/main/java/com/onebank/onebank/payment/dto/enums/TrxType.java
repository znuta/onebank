package com.onebank.onebank.payment.dto.enums;

public enum TrxType {
    CREDIT("CREDIT"),

    FORWARD_BALANCE("FORWARD_BALANCE"),
    DEBIT("DEBIT");

    private final String trxType;

    TrxType(String trxType) {
        this.trxType = trxType;
    }

    public String getTrxType() {
        return this.trxType;
    }
}
