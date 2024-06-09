package com.onebank.onebank.payment.repository;

import com.onebank.onebank.payment.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}