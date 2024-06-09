package com.onebank.onebank.payment.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.dto.output.StandardResponseDTO;
import com.onebank.onebank.payment.entity.PaymentAccount;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NameEnquiryDTO extends StandardResponseDTO {

    private Long id;
    private String accountNumber;
    private String currency;
    private String name;

    private Object data;


    public NameEnquiryDTO(PaymentAccount paymentAccount) {
        this.id = paymentAccount.getId();
        this.accountNumber = paymentAccount.getAccountNumber();
        this.currency = paymentAccount.getCurrency();
        this.name = paymentAccount.getName();
    }

    public NameEnquiryDTO(Status status) {
        super(status);
    }

    public NameEnquiryDTO(Status status, Object data) {
        super(status);
        this.data = data;
    }

}
