package com.onebank.onebank.payment.service.implementation;

import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.auth.repository.UserRepository;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.payment.dto.output.LedgerDTO;
import com.onebank.onebank.payment.entity.Ledger;
import com.onebank.onebank.payment.entity.PaymentAccount;
import com.onebank.onebank.payment.repository.LedgerRepository;
import com.onebank.onebank.payment.repository.PaymentAccountRepository;
import com.onebank.onebank.payment.service.PaymentTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class LedgerServiceImpTest {

    @Mock
    private LedgerRepository ledgerRepository;

    @Mock
    private PaymentAccountRepository paymentAccountRepository;

    @Mock
    private PaymentTransactionService transactionService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LedgerServiceImp ledgerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateLedger_NewLedgerCreated() {
        PaymentAccount paymentAccount = new PaymentAccount();
        Ledger oldLedger = new Ledger();
        oldLedger.setDateCreated(LocalDate.now().minusDays(1));
        oldLedger.setBalance(BigDecimal.valueOf(100));
        paymentAccount.setCurrentLedger(oldLedger);

        when(ledgerRepository.save(any(Ledger.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentAccountRepository.save(any(PaymentAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ledger newLedger = ledgerService.createLedger(paymentAccount);

        assertNotNull(newLedger);
        assertEquals(BigDecimal.ZERO, newLedger.getBalance());
        verify(transactionService, times(1)).createAuditTransaction(paymentAccount, newLedger, oldLedger.getBalance());
        assertTrue(oldLedger.isClosed());
        assertEquals(newLedger, paymentAccount.getCurrentLedger());
    }

    @Test
    public void testCreateLedger_OldLedgerReturned() {
        PaymentAccount paymentAccount = new PaymentAccount();
        Ledger oldLedger = new Ledger();
        oldLedger.setDateCreated(LocalDate.now());
        paymentAccount.setCurrentLedger(oldLedger);

        Ledger result = ledgerService.createLedger(paymentAccount);

        assertEquals(oldLedger, result);
        verify(ledgerRepository, never()).save(any(Ledger.class));
        verify(transactionService, never()).createAuditTransaction(any(), any(), any());
    }

    @Test
    public void testGetAccountLedgerByDate_LedgerFound() throws AccountNotFoundException {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        PaymentAccount paymentAccount = new PaymentAccount();
        Ledger ledger = new Ledger();
        ledger.setDateCreated(LocalDate.now());

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(paymentAccountRepository.findByIdAndUser(anyLong(), any(AppUser.class))).thenReturn(Optional.of(paymentAccount));
        when(ledgerRepository.findByPaymentAccountAndDateCreated(any(PaymentAccount.class), any(LocalDate.class))).thenReturn(Optional.of(ledger));

        LedgerDTO result = ledgerService.getAccountLedgerByDate(1L, LocalDate.now().toString(), "testuser");

        assertNotNull(result);
        assertEquals(Status.SUCCESS, result.getStatus());
        assertNotNull(result);
    }

    @Test
    public void testGetAccountLedgerByDate_LedgerNotFound() throws AccountNotFoundException {
        AppUser user = new AppUser();
        LedgerDTO ledgerDTO = new LedgerDTO(Status.SUCCESS);
        user.setUsername("testuser");
        PaymentAccount paymentAccount = new PaymentAccount();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(paymentAccountRepository.findByIdAndUser(anyLong(), any(AppUser.class))).thenReturn(Optional.of(paymentAccount));
        when(ledgerRepository.findByPaymentAccountAndDateCreated(any(PaymentAccount.class), any(LocalDate.class))).thenReturn(Optional.empty());

        LedgerDTO result = ledgerService.getAccountLedgerByDate(1L, LocalDate.now().toString(), "testuser");

        assertNotNull(result);
        assertEquals(Status.SUCCESS, result.getStatus());
        assertEquals(ledgerDTO,result);
    }

    @Test
    public void testGetAccountLedgerByDate_AccountNotFound() {
        AppUser user = new AppUser();
        user.setUsername("testuser");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(paymentAccountRepository.findByIdAndUser(anyLong(), any(AppUser.class))).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            ledgerService.getAccountLedgerByDate(1L, LocalDate.now().toString(), "testuser");
        });
    }
}
