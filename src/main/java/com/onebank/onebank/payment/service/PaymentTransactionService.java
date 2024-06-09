package com.onebank.onebank.payment.service;

import com.onebank.onebank.payment.dto.input.TransferRequestDto;
import com.onebank.onebank.payment.dto.output.TransactionResponseDTO;
import com.onebank.onebank.payment.entity.Ledger;
import com.onebank.onebank.payment.entity.PaymentAccount;
import com.onebank.onebank.payment.entity.PaymentTransaction;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;

public interface PaymentTransactionService {

    TransactionResponseDTO getTransactions(String username, Long accountId, int page, int size) throws AccountNotFoundException;
    TransactionResponseDTO filterTransactions(String username,String status, Long accountId, String startDate, String endDate) throws AccountNotFoundException;

    PaymentTransaction createSourceTransaction(PaymentAccount sourceAccount, TransferRequestDto transferRequestDto);

    PaymentTransaction createDestinationTransaction(PaymentAccount destinationAccount, TransferRequestDto transferRequestDto);

    PaymentTransaction createAuditTransaction(PaymentAccount paymentAccount, Ledger newLedger, BigDecimal balance);
}
