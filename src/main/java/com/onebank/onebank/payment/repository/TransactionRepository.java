package com.onebank.onebank.payment.repository;

import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.payment.entity.Ledger;
import com.onebank.onebank.payment.entity.PaymentAccount;
import com.onebank.onebank.payment.entity.PaymentTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    List<PaymentTransaction> findByLedger(Ledger ledger);

    Page<PaymentTransaction> findByPaymentAccountOrderByDateCreatedDesc(PaymentAccount paymentAccount, Pageable pageable);

    List<PaymentTransaction> findByPaymentAccountAndStatusAndDateCreatedBetween(
             PaymentAccount paymentAccount,
             String status,
            LocalDateTime startDate, LocalDateTime endDate);
}
