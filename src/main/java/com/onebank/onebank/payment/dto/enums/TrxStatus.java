package com.onebank.onebank.payment.dto.enums;

public enum TrxStatus {
    SUCCESSFUL("SUCCESSFUL"),
    FAILED("FAILED"),
    INSUFFICIENT_FUND("INSUFFICIENT_FUND");

    private final String status;

    TrxStatus(String status) {
        this.status = status;
    }

    public String getTrxStatus() {
        return this.status;
    }
}
