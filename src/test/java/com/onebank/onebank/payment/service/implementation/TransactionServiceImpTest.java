package com.onebank.onebank.payment.service.implementation;

import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.auth.service.UserService;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.payment.dto.enums.TrxStatus;
import com.onebank.onebank.payment.dto.input.TransferRequestDto;
import com.onebank.onebank.payment.dto.output.TransactionResponseDTO;
import com.onebank.onebank.payment.entity.Ledger;
import com.onebank.onebank.payment.entity.PaymentAccount;
import com.onebank.onebank.payment.entity.PaymentTransaction;
import com.onebank.onebank.payment.repository.LedgerRepository;
import com.onebank.onebank.payment.repository.PaymentAccountRepository;
import com.onebank.onebank.payment.repository.TransactionRepository;
import com.onebank.onebank.payment.service.LedgerService;
import com.onebank.onebank.util.PaymentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.security.auth.login.AccountNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImpTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private LedgerService ledgerService;

    @Mock
    private LedgerRepository ledgerRepository;

    @Mock
    private PaymentAccountRepository paymentAccountRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionServiceImp transactionService;

    private PaymentAccount paymentAccount;
    private AppUser user;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setUsername("testuser");

        paymentAccount = new PaymentAccount();
        paymentAccount.setId(1L);
        paymentAccount.setUser(user);
        paymentAccount.setBalance(BigDecimal.valueOf(1000));
    }

    @Test
    void testGetTransactions_Success() throws AccountNotFoundException {
        when(paymentAccountRepository.findById(anyLong())).thenReturn(Optional.of(paymentAccount));
        Pageable pageable = PageRequest.of(0, 10);
        PaymentTransaction transaction = new PaymentTransaction();
        Page<PaymentTransaction> transactions = new PageImpl<>(Collections.singletonList(transaction));
        when(transactionRepository.findByPaymentAccountOrderByDateCreatedDesc(any(PaymentAccount.class), any(Pageable.class))).thenReturn(transactions);

        TransactionResponseDTO responseDTO = transactionService.getTransactions("testuser", 1L, 0, 10);
        List<TransactionResponseDTO> transactionResponseDTOList = (List<TransactionResponseDTO>) responseDTO.getData();
        assertNotNull(responseDTO);
        assertEquals(Status.SUCCESS, responseDTO.getStatus());
        assertEquals(1, transactionResponseDTOList.size());
        verify(paymentAccountRepository, times(1)).findById(anyLong());
        verify(transactionRepository, times(1)).findByPaymentAccountOrderByDateCreatedDesc(any(PaymentAccount.class), any(Pageable.class));
    }

    @Test
    void testGetTransactions_AccountNotFound() {
        when(paymentAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> transactionService.getTransactions("testuser", 1L, 0, 10));
        verify(paymentAccountRepository, times(1)).findById(anyLong());
        verify(transactionRepository, times(0)).findByPaymentAccountOrderByDateCreatedDesc(any(PaymentAccount.class), any(Pageable.class));
    }

    @Test
    void testGetTransactions_UserNotAuthorized() {
        AppUser differentUser = new AppUser();
        differentUser.setUsername("differentuser");
        paymentAccount.setUser(differentUser);
        when(paymentAccountRepository.findById(anyLong())).thenReturn(Optional.of(paymentAccount));

        assertThrows(IllegalArgumentException.class, () -> transactionService.getTransactions("testuser", 1L, 0, 10));
        verify(paymentAccountRepository, times(1)).findById(anyLong());
        verify(transactionRepository, times(0)).findByPaymentAccountOrderByDateCreatedDesc(any(PaymentAccount.class), any(Pageable.class));
    }

}
