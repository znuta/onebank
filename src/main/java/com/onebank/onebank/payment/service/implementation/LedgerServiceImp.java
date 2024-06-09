package com.onebank.onebank.payment.service.implementation;

import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.auth.repository.UserRepository;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.payment.dto.output.LedgerDTO;
import com.onebank.onebank.payment.entity.Ledger;
import com.onebank.onebank.payment.entity.PaymentAccount;
import com.onebank.onebank.payment.repository.LedgerRepository;
import com.onebank.onebank.payment.repository.PaymentAccountRepository;
import com.onebank.onebank.payment.service.LedgerService;
import com.onebank.onebank.payment.service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class LedgerServiceImp implements LedgerService {

    private final LedgerRepository ledgerRepository;
    private final PaymentAccountRepository paymentAccountRepository;
    private final PaymentTransactionService transactionService;
    private final UserRepository userRepository;

    @Autowired
    public LedgerServiceImp(
            LedgerRepository ledgerRepository,
            PaymentAccountRepository paymentAccountRepository,
            @Lazy PaymentTransactionService transactionService,
            UserRepository userRepository) {
        this.ledgerRepository = ledgerRepository;
        this.paymentAccountRepository = paymentAccountRepository;
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new ledger for the given payment account if the current ledger's date is before today.
     * Also creates an audit transaction for the old ledger balance and closes the old ledger.
     *
     * @param paymentAccount the payment account for which the new ledger is to be created
     * @return the newly created ledger
     */
    @Override
    public Ledger createLedger(PaymentAccount paymentAccount) {
        Ledger oldLedger = paymentAccount.getCurrentLedger();
        if (oldLedger.getDateCreated().isBefore(LocalDate.now())) {
            Ledger newLedger = new Ledger();
            newLedger.setBalance(BigDecimal.ZERO);
            newLedger.setCredit(BigDecimal.ZERO);
            newLedger.setDebit(BigDecimal.ZERO);
            newLedger.setPaymentAccount(paymentAccount);
            newLedger.setClosed(false);
            newLedger.setDateCreated(LocalDate.now());
            newLedger = ledgerRepository.save(newLedger);

            // Delegate transaction creation to TransactionService
            transactionService.createAuditTransaction(paymentAccount, newLedger, oldLedger.getBalance());

            oldLedger.setClosed(true);
            ledgerRepository.save(oldLedger);

            paymentAccount.setCurrentLedger(newLedger);
            paymentAccountRepository.save(paymentAccount);

            return newLedger;
        } else {
            return oldLedger;
        }
    }

    /**
     * Retrieves the ledger for a given payment account and date.
     *
     * @param accountId the ID of the payment account
     * @param date the date for which the ledger is to be retrieved
     * @param username the username of the account owner
     * @return a LedgerDTO containing the status and ledger information
     * @throws AccountNotFoundException if the account or user is not found
     */
    @Override
    public LedgerDTO getAccountLedgerByDate(Long accountId, String date, String username) throws AccountNotFoundException {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        PaymentAccount paymentAccount = paymentAccountRepository.findByIdAndUser(accountId, user)
                .orElseThrow(() -> new AccountNotFoundException("Account with the id and user not found"));
        Optional<Ledger> ledger = ledgerRepository.findByPaymentAccountAndDateCreated(paymentAccount, LocalDate.parse(date));
        if (ledger.isEmpty()) {
            return new LedgerDTO(Status.SUCCESS);
        } else {
            return new LedgerDTO(Status.SUCCESS, new LedgerDTO(ledger.get()));
        }
    }
}
