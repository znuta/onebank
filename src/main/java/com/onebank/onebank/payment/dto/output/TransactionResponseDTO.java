package com.onebank.onebank.payment.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.dto.output.StandardResponseDTO;
import com.onebank.onebank.payment.entity.Ledger;
import com.onebank.onebank.payment.entity.PaymentAccount;
import com.onebank.onebank.payment.entity.PaymentTransaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponseDTO extends StandardResponseDTO {

    private Long id;

    private Ledger ledger;

    private PaymentAccount paymentAccount;

    private AppUser user;

    private String trxType;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreated;

    private String transactionStatus;

    private String transactionReference;

    private BigDecimal transactionFee;

    private BigDecimal billedAmount;

    private boolean commissionWorthy;

    private BigDecimal commission;

    private Object data;


    public TransactionResponseDTO(Status status) {
        super(status);
    }

    public TransactionResponseDTO(Status status, Object data) {
        super(status);
        this.data = data;
    }
    public TransactionResponseDTO(PaymentTransaction paymentTransaction) {
        this.id = paymentTransaction.getId();
        this.trxType = paymentTransaction.getTrxType();
        this.description = paymentTransaction.getDescription();
        this.dateCreated = paymentTransaction.getDateCreated();
        this.transactionStatus = paymentTransaction.getStatus();
        this.transactionReference = paymentTransaction.getTransactionReference();
        this.transactionFee = paymentTransaction.getTransactionFee();
        this.billedAmount = paymentTransaction.getBilledAmount();
        this.commissionWorthy = paymentTransaction.isCommissionWorthy();
        this.commission = paymentTransaction.getCommission();
    }

}
