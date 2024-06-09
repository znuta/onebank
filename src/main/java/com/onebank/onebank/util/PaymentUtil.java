package com.onebank.onebank.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


public class PaymentUtil {


    private static BigDecimal cmFeePercent = BigDecimal.valueOf(0.20);

    private static BigDecimal trxFeePercent = BigDecimal.valueOf(0.005);
    private static BigDecimal capAt = BigDecimal.valueOf(100);

    public static BigDecimal calculateTransactionFee(BigDecimal amount) {
        return amount.multiply(trxFeePercent).min(capAt);
    }

    public static BigDecimal calculateCommissionFee(BigDecimal transactionFee) {
        return transactionFee.multiply(cmFeePercent);
    }
}
