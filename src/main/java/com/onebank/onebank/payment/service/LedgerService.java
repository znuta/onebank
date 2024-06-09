package com.onebank.onebank.payment.service;

import com.onebank.onebank.payment.dto.output.LedgerDTO;
import com.onebank.onebank.payment.entity.Ledger;
import com.onebank.onebank.payment.entity.PaymentAccount;

import javax.security.auth.login.AccountNotFoundException;

public interface LedgerService {
    Ledger createLedger(PaymentAccount paymentAccount);

    LedgerDTO getAccountLedgerByDate(Long accountId, String date, String username) throws AccountNotFoundException;
}
