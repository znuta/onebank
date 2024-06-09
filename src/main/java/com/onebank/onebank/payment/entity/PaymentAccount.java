package com.onebank.onebank.payment.entity;

import com.onebank.onebank.auth.entity.AppUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "payment_account")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaymentAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;
    private String currency;
    private BigDecimal balance;
    private String accountNumber;

    @OneToMany(mappedBy = "paymentAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ledger> ledgers = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "current_ledger_id")
    private Ledger currentLedger;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    private LocalDateTime dateCreated;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentAccount that = (PaymentAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
