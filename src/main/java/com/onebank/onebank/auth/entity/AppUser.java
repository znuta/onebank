package com.onebank.onebank.auth.entity;

import com.onebank.onebank.auth.dto.enums.RoleType;
import com.onebank.onebank.payment.entity.PaymentAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private Set<ApiKey> apiKeys;

    @Column(name = "role")
    private RoleType role;

    @PrePersist
    protected void onCreate() {
        if (this.role == null) {
            this.role = RoleType.valueOf(RoleType.ROLE_USER.getRole());
        }
    }
}
