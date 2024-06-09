package com.onebank.onebank.payment.service.implementation;

import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.auth.service.UserService;
import com.onebank.onebank.payment.dto.enums.CurrencyType;
import com.onebank.onebank.payment.dto.input.PaymentAccountRequestDto;
import com.onebank.onebank.payment.dto.input.TransferRequestDto;
import com.onebank.onebank.payment.dto.output.PaymentAccountDTO;
import com.onebank.onebank.payment.dto.output.TransferResponseDTO;
import com.onebank.onebank.payment.entity.PaymentAccount;
import com.onebank.onebank.payment.repository.LedgerRepository;
import com.onebank.onebank.payment.repository.PaymentAccountRepository;
import com.onebank.onebank.payment.service.PaymentTransactionService;
import com.onebank.onebank.payment.service.implementation.PaymentServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PaymentServiceImpTest {

    @Mock
    private PaymentAccountRepository paymentAccountRepository;
    @Mock
    private LedgerRepository ledgerRepository;
    @Mock
    private PaymentTransactionService transactionService;
    @Mock
    private UserService userService;
    @Mock
    private AppUser user;

    @InjectMocks
    private PaymentServiceImp paymentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPaymentAccounts() {
        when(paymentAccountRepository.findByUser(user)).thenReturn(Collections.singletonList(new PaymentAccount()));
        List<PaymentAccountDTO> result = paymentService.getPaymentAccounts(user);
        assertEquals(1, result.size());
    }

    @Test
    public void testCreatePaymentAccount() {

        // Mock the userService to return a mock AppUser
        AppUser user = new AppUser(); // Create a mock AppUser object
        when(userService.findByUsername(anyString())).thenReturn(Optional.of(user));

        PaymentAccountRequestDto requestDto = new PaymentAccountRequestDto();
        requestDto.setName("Test Account");
        requestDto.setCurrency(CurrencyType.valueOf(CurrencyType.NGN.getCurrency()));
        requestDto.setUser(user);

        PaymentAccount newPayment = new PaymentAccount();
        newPayment.setName("Test Account");
        // Mock the repository to return a new PaymentAccount
        when(paymentAccountRepository.save(any())).thenReturn(newPayment);

        // Use any valid username for the test
        String username = "testuser";
        PaymentAccountDTO result = paymentService.createPaymentAccount(requestDto, username);
        assertEquals("Test Account", result.getName());
    }


    @Test
    public void testNipaccountvalidate() throws AccountNotFoundException {
        when(paymentAccountRepository.findByAccountNumber("123")).thenReturn(Optional.of(new PaymentAccount()));
        assertEquals("SUCCESS", paymentService.nipaccountvalidate("123").getStatus().toString());
    }

    @Test
    public void testNipaccountvalidateInvalidAccount() {
        when(paymentAccountRepository.findByAccountNumber("invalid")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> paymentService.nipaccountvalidate("invalid"));
    }
}
