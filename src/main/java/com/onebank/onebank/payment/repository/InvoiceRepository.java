package com.onebank.onebank.payment.repository;

import com.onebank.onebank.payment.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
