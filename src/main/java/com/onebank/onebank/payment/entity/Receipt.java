package com.onebank.onebank.payment.entity;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "receipt")
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;

    @ManyToOne
    private Ledger ledger;

    @OneToOne
    private PaymentTransaction paymentTransaction;

    private BigDecimal amount;
}
