package com.onebank.onebank.scheduler;

import com.onebank.onebank.payment.dto.enums.TrxStatus;
import com.onebank.onebank.payment.dto.enums.TrxType;
import com.onebank.onebank.payment.entity.Ledger;
import com.onebank.onebank.payment.entity.PaymentAccount;
import com.onebank.onebank.payment.entity.PaymentTransaction;
import com.onebank.onebank.payment.repository.LedgerRepository;
import com.onebank.onebank.payment.repository.PaymentAccountRepository;
import com.onebank.onebank.payment.repository.TransactionRepository;
import com.onebank.onebank.util.PaymentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class LedgerAuditScheduler {

    private final LedgerRepository ledgerRepository;
    private final TransactionRepository transactionRepository;

    private final PaymentAccountRepository paymentAccountRepository;

    @Autowired
    public LedgerAuditScheduler(LedgerRepository ledgerRepository, TransactionRepository transactionRepository, PaymentAccountRepository paymentAccountRepository) {
        this.ledgerRepository = ledgerRepository;
        this.transactionRepository = transactionRepository;
        this.paymentAccountRepository = paymentAccountRepository;
    }

    public Ledger forwardLedger(PaymentAccount paymentAccount) {
        Ledger oldLedger = paymentAccount.getCurrentLedger();
        if (oldLedger.getDateCreated().isBefore(LocalDate.now())) {
            Ledger newLedger = new Ledger();
            newLedger.setBalance(BigDecimal.ZERO);
            newLedger.setPaymentAccount(paymentAccount);
            newLedger.setClosed(false);
            newLedger.setDateCreated(LocalDate.now());
            newLedger = ledgerRepository.save(newLedger);

            PaymentTransaction transaction = new PaymentTransaction();
            transaction.setTransactionFee(oldLedger.getBalance());
            transaction.setDescription("Audited Ledger balance");
            transaction.setPaymentAccount(paymentAccount);
            transaction.setTrxType(TrxType.FORWARD_BALANCE.getTrxType());
            transaction.setUser(paymentAccount.getUser());
            transaction.setLedger(newLedger);

            transactionRepository.save(transaction);

            oldLedger.setClosed(true);
            ledgerRepository.save(oldLedger);

            paymentAccount.setCurrentLedger(newLedger);
            paymentAccountRepository.save(paymentAccount);

            return newLedger;
        } else {
            return oldLedger;
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
    public void performLedgerAuditAndForward() {
        List<Ledger> openLedgers = ledgerRepository.findByClosedFalse();

        for (Ledger ledger : openLedgers) {
            if (ledger.getDateCreated().isBefore(LocalDate.now())) {
                BigDecimal balanceToForward = ledger.getBalance();
                List<PaymentTransaction> transactions = transactionRepository.findByLedger(ledger);

                for (PaymentTransaction transaction : transactions) {
                    if (transaction.getStatus() == TrxStatus.SUCCESSFUL.getTrxStatus()) {
                        transaction.setCommissionWorthy(true);
                        transaction.setCommission(PaymentUtil.calculateCommissionFee(transaction.getTransactionFee()));
                        // Perform necessary checks on each transaction
                        // For example, verify consistency with ledger balance and account balance
                        if (transaction.getTrxType() == TrxType.CREDIT.getTrxType()) {
                            balanceToForward = balanceToForward.subtract(transaction.getBilledAmount());
                        } else if (transaction.getTrxType() == TrxType.DEBIT.getTrxType()) {
                            balanceToForward = balanceToForward.add(transaction.getBilledAmount());
                        }
                        transaction.setSettled(true);
                        transactionRepository.save(transaction);

                    } else {
                        // Handle unsuccessful transactions
                    }
                }

                if (balanceToForward.compareTo(BigDecimal.ZERO) != 0) {

                    forwardLedger(ledger.getPaymentAccount());
                }
            }
        }
    }


}
