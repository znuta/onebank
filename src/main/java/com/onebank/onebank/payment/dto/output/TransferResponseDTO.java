package com.onebank.onebank.payment.dto.output;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.onebank.onebank.auth.dto.output.UserAuthResponseDTO;
import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.dto.output.StandardResponseDTO;
import com.onebank.onebank.payment.entity.PaymentTransaction;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferResponseDTO extends StandardResponseDTO {
    public TransferResponseDTO(PaymentTransaction paymentTransaction) {

        this.transactionStatus = paymentTransaction.getStatus();
        this.amount = paymentTransaction.getBilledAmount();
        this.reference = paymentTransaction.getTransactionReference();
        this.sourceAccountNumber = paymentTransaction.getPaymentAccount().getAccountNumber();
    }

    private Object data;
    private String transactionStatus;
    private BigDecimal amount;
    private String reference;
    private String sourceAccountName;
    private String sourceAccountNumber;
    private String destinationAccountName;

    public TransferResponseDTO(Status status) {
        super(status);
    }

    public TransferResponseDTO(Status status, Object data) {
        super(status);
        this.data = data;
    }

}


