package com.onebank.onebank.payment.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.dto.output.StandardResponseDTO;
import com.onebank.onebank.payment.entity.Ledger;
import com.onebank.onebank.payment.entity.PaymentAccount;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LedgerDTO extends StandardResponseDTO {

    private Long id;
    private LocalDate dateCreated;
    private boolean closed;
    private BigDecimal balance;
    private BigDecimal credit;
    private BigDecimal debit;
    private Object data;
    public LedgerDTO(Status status) {
        super(status);
    }
    public LedgerDTO(Ledger ledger) {
        if (ledger != null) {
            this.id = ledger.getId();
            this.dateCreated = ledger.getDateCreated();
            this.balance = ledger.getBalance();
            this.closed = ledger.isClosed();
            this.credit = ledger.getCredit();
            this.debit = ledger.getDebit();
        }

    }

    public LedgerDTO(Status status, LedgerDTO ledgerDTO) {
        super(status);
        this.data = ledgerDTO;
    }


}
