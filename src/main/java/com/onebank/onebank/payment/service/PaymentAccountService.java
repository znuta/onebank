package com.onebank.onebank.payment.service;

import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.payment.dto.input.PaymentAccountRequestDto;
import com.onebank.onebank.payment.dto.input.TransferRequestDto;
import com.onebank.onebank.payment.dto.output.NameEnquiryDTO;
import com.onebank.onebank.payment.dto.output.PaymentAccountDTO;
import com.onebank.onebank.payment.dto.output.TransferResponseDTO;
import com.onebank.onebank.payment.entity.PaymentAccount;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Optional;

public interface PaymentAccountService {
    List<PaymentAccountDTO> getPaymentAccounts(AppUser user);

    TransferResponseDTO intraTransfer(TransferRequestDto transferRequestDto);

    TransferResponseDTO deposit(TransferRequestDto transferRequestDto);

    Optional<PaymentAccount> findById(Long id);

    PaymentAccountDTO createPaymentAccount(PaymentAccountRequestDto paymentAccountRequestDto, String username);

    NameEnquiryDTO nipaccountvalidate(String accountNumber) throws AccountNotFoundException;
}
