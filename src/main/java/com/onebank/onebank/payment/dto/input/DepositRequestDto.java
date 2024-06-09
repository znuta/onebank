package com.onebank.onebank.payment.dto.input;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DepositRequestDto {

    private String destinationAccountNumber;
    private String destinationAccountName;
    private BigDecimal amount;
    private String remark;

}
