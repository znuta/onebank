package com.onebank.onebank.payment.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@Table(name = "ledger")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private LocalDate dateCreated;

    private BigDecimal balance;

    private BigDecimal credit;

    private BigDecimal debit;

    @ManyToOne
    @JoinColumn(name = "payment_account_id")
    private PaymentAccount paymentAccount;

    @OneToMany(mappedBy = "ledger", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentTransaction> paymentTransactions;

    private boolean closed;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ledger ledger = (Ledger) o;
        return Objects.equals(id, ledger.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
