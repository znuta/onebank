package com.onebank.onebank.payment.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onebank.onebank.dto.output.StandardResponseDTO;
import com.onebank.onebank.payment.entity.Ledger;
import com.onebank.onebank.payment.entity.PaymentAccount;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentAccountDTO extends StandardResponseDTO {
    private Long id;
    private String accountNumber;
    private String currency;

    private String name;
    private BigDecimal balance;

    private LedgerDTO ledger;

    public PaymentAccountDTO(PaymentAccount paymentAccount) {
        this.id = paymentAccount.getId();
        this.accountNumber = paymentAccount.getAccountNumber();
        this.balance = paymentAccount.getBalance();
        this.currency = paymentAccount.getCurrency();
        this.name = paymentAccount.getName();
        this.ledger = new  LedgerDTO(paymentAccount.getCurrentLedger());
    }

}
