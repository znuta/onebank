package com.onebank.onebank.payment.dto.input;

import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.payment.dto.enums.CurrencyType;
import com.onebank.onebank.payment.entity.Ledger;
import com.onebank.onebank.payment.entity.PaymentAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAccountRequestDto {
    private String name;
    private CurrencyType currency;
    private AppUser user;

    public static PaymentAccount fromDto(PaymentAccountRequestDto paymentAccountRequestDto){
        PaymentAccount paymentAccount = new PaymentAccount();
        paymentAccount.setBalance(BigDecimal.valueOf(0));
        paymentAccount.setCurrency(paymentAccountRequestDto.getCurrency().getCurrency());
        paymentAccount.setName(paymentAccountRequestDto.getName());
        paymentAccount.setUser(paymentAccountRequestDto.getUser());

        return paymentAccount;
    }
}
