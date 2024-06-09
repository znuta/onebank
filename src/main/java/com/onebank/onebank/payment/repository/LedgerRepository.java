package com.onebank.onebank.payment.repository;

import com.onebank.onebank.payment.entity.Ledger;
import com.onebank.onebank.payment.entity.PaymentAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    Ledger findByDateCreated(LocalDate dateCreated);
    List<Ledger>  findByClosedFalse();

    List<Ledger> findByPaymentAccount(PaymentAccount paymentAccount);

    Optional<Ledger> findByPaymentAccountAndDateCreated(PaymentAccount paymentAccount, LocalDate dateCreated);

}
