package com.onebank.onebank.payment.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.onebank.onebank.auth.entity.AppUser;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transaction")
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ledger_id")
    @JsonBackReference
    private Ledger ledger;

    @ManyToOne
    @JoinColumn(name = "payment_account_id")
    @JsonBackReference
    private PaymentAccount paymentAccount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private AppUser user;

    private String trxType;

    private String description;

    private LocalDateTime dateCreated;

    private String status;

    private String transactionReference;

    private BigDecimal transactionFee;

    private BigDecimal billedAmount;

    private boolean commissionWorthy;

    private BigDecimal commission;

    private boolean settled;
}