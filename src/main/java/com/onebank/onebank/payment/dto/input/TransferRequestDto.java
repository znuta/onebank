package com.onebank.onebank.payment.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TransferRequestDto {
    private String sourceAccountNumber;
    private String sourceAccountName;
    private String destinationAccountNumber;
    private String destinationAccountName;
    private BigDecimal amount;
    private String remark;

}
