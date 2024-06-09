package com.onebank.onebank.payment.repository;

import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.payment.entity.PaymentAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentAccountRepository  extends JpaRepository<PaymentAccount, Long> {
    List<PaymentAccount> findByUser(AppUser user);

    PaymentAccount findFirstByOrderByIdDesc();
    Optional<PaymentAccount> findByIdAndUser(Long id, AppUser user);
   Optional<PaymentAccount> findByAccountNumber(String accountNumber);
}