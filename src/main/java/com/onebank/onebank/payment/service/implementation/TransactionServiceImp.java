package com.onebank.onebank.payment.service.implementation;

import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.auth.service.UserService;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.payment.dto.enums.TrxStatus;
import com.onebank.onebank.payment.dto.enums.TrxType;
import com.onebank.onebank.payment.dto.input.TransferRequestDto;
import com.onebank.onebank.payment.dto.output.TransactionResponseDTO;
import com.onebank.onebank.payment.entity.Ledger;
import com.onebank.onebank.payment.entity.PaymentAccount;
import com.onebank.onebank.payment.entity.PaymentTransaction;
import com.onebank.onebank.payment.repository.LedgerRepository;
import com.onebank.onebank.payment.repository.PaymentAccountRepository;
import com.onebank.onebank.payment.repository.TransactionRepository;
import com.onebank.onebank.payment.service.LedgerService;
import com.onebank.onebank.payment.service.PaymentTransactionService;
import com.onebank.onebank.util.DateParser;
import com.onebank.onebank.util.PaymentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImp implements PaymentTransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImp.class);
    private static final Random random = new Random();

    private final TransactionRepository transactionRepository;
    private final LedgerService ledgerService;
    private final LedgerRepository ledgerRepository;
    private final PaymentAccountRepository paymentAccountRepository;
    private final UserService userService;

    @Autowired
    public TransactionServiceImp(TransactionRepository transactionRepository, LedgerService ledgerService, LedgerRepository ledgerRepository,
                                 PaymentAccountRepository paymentAccountRepository, @Lazy UserService userService) {
        this.transactionRepository = transactionRepository;
        this.ledgerService = ledgerService;
        this.ledgerRepository = ledgerRepository;
        this.paymentAccountRepository = paymentAccountRepository;
        this.userService = userService;
    }

    /**
     * Retrieves transactions for a specific user and account.
     *
     * @param username The username of the user.
     * @param accountId The ID of the payment account.
     * @param page The page number for pagination.
     * @param size The page size for pagination.
     * @return A response DTO containing the transaction details.
     * @throws AccountNotFoundException If the account is not found.
     */
    public TransactionResponseDTO getTransactions(String username, Long accountId, int page, int size) throws AccountNotFoundException {
        Optional<PaymentAccount> paymentAccountOptional = paymentAccountRepository.findById(accountId);

        if (paymentAccountOptional.isEmpty()) {
            throw new AccountNotFoundException("Payment account not found");
        }

        PaymentAccount paymentAccount = paymentAccountOptional.get();

        if (!username.equals(paymentAccount.getUser().getUsername())) {
            throw new IllegalArgumentException("User not authorized for this payment account");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentTransaction> transactions = transactionRepository.findByPaymentAccountOrderByDateCreatedDesc(paymentAccount, pageable);

        List<TransactionResponseDTO> transactionResponseDTOS = transactions.stream()
                .map(TransactionResponseDTO::new)
                .collect(Collectors.toList());
        return new TransactionResponseDTO(Status.SUCCESS, transactionResponseDTOS);
    }

    /**
     * Filters transactions for a specific user and account based on status and date range.
     *
     * @param username The username of the user.
     * @param status The status of the transactions to filter.
     * @param accountId The ID of the payment account.
     * @param startDate The start date for filtering.
     * @param endDate The end date for filtering.
     * @return A response DTO containing the filtered transaction details.
     * @throws AccountNotFoundException If the account is not found.
     */
    public TransactionResponseDTO filterTransactions(String username, String status, Long accountId, String startDate, String endDate) throws AccountNotFoundException {
        Optional<PaymentAccount> paymentAccountOptional = paymentAccountRepository.findById(accountId);

        if (paymentAccountOptional.isEmpty()) {
            throw new AccountNotFoundException("Payment account not found");
        }

        PaymentAccount paymentAccount = paymentAccountOptional.get();

        if (!username.equals(paymentAccount.getUser().getUsername())) {
            throw new IllegalArgumentException("User not authorized for this payment account");
        }

        LocalDateTime startDateTime = startDate != null ? DateParser.parseDate(startDate) : null;
        LocalDateTime endDateTime = endDate != null ? DateParser.parseDate(endDate) : null;

        List<PaymentTransaction> transactions = transactionRepository.findByPaymentAccountAndStatusAndDateCreatedBetween(paymentAccount, status, startDateTime, endDateTime);

        List<TransactionResponseDTO> transactionResponseDTOS = transactions.stream()
                .map(TransactionResponseDTO::new)
                .collect(Collectors.toList());

        return new TransactionResponseDTO(Status.SUCCESS, transactionResponseDTOS);
    }

    /**
     * Generates a unique transaction reference.
     *
     * @return The generated transaction reference.
     */
    public static synchronized String generateTransactionReference() {
        long timestamp = Instant.now().toEpochMilli();
        int randomNumber = random.nextInt(100000);
        return String.format("%d-%05d", timestamp, randomNumber);
    }

    /**
     * Creates a debit transaction for the source account.
     *
     * @param sourceAccount The source payment account.
     * @param transferRequestDto The transfer request details.
     * @return The created payment transaction.
     */
    public PaymentTransaction createSourceTransaction(PaymentAccount sourceAccount, TransferRequestDto transferRequestDto) {
        Ledger currentLedger = ledgerService.createLedger(sourceAccount);
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionFee(PaymentUtil.calculateTransactionFee(transferRequestDto.getAmount()));
        transaction.setBilledAmount(transferRequestDto.getAmount().add(transaction.getTransactionFee()));
        transaction.setDescription(transferRequestDto.getRemark());
        transaction.setPaymentAccount(sourceAccount);
        transaction.setTrxType(TrxType.DEBIT.getTrxType());
        transaction.setUser(sourceAccount.getUser());
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setDateCreated(LocalDateTime.now());
        transaction.setLedger(currentLedger);

        if (sourceAccount.getBalance().compareTo(transferRequestDto.getAmount()) < 0) {
            transaction.setStatus(TrxStatus.INSUFFICIENT_FUND.getTrxStatus());
        } else {
            transaction.setStatus(TrxStatus.SUCCESSFUL.getTrxStatus());
            transaction.setCommissionWorthy(true);
            transaction.setCommission(PaymentUtil.calculateCommissionFee(transaction.getTransactionFee()));

            // Update source account balance
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(transaction.getBilledAmount()));
            paymentAccountRepository.save(sourceAccount);

            // Update ledger
            currentLedger.setDebit(currentLedger.getDebit().add(transferRequestDto.getAmount()));
            currentLedger.setBalance(currentLedger.getBalance().subtract(transferRequestDto.getAmount()));
            ledgerRepository.save(currentLedger);
        }

        return transactionRepository.save(transaction);
    }

    /**
     * Creates a credit transaction for the destination account.
     *
     * @param destinationAccount The destination payment account.
     * @param transferRequestDto The transfer request details.
     * @return The created payment transaction.
     */
    public PaymentTransaction createDestinationTransaction(PaymentAccount destinationAccount, TransferRequestDto transferRequestDto) {
        Ledger currentLedger = ledgerService.createLedger(destinationAccount);
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionFee(BigDecimal.ZERO);
        transaction.setBilledAmount(transferRequestDto.getAmount());
        transaction.setDescription(transferRequestDto.getRemark());
        transaction.setPaymentAccount(destinationAccount);
        transaction.setTrxType(TrxType.CREDIT.getTrxType());
        transaction.setUser(destinationAccount.getUser());
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setDateCreated(LocalDateTime.now());
        transaction.setStatus(TrxStatus.SUCCESSFUL.getTrxStatus());
        transaction.setLedger(currentLedger);

        // Update destination account balance
        destinationAccount.setBalance(destinationAccount.getBalance().add(transferRequestDto.getAmount()));
        paymentAccountRepository.save(destinationAccount);

        // Update ledger
        currentLedger.setCredit(currentLedger.getCredit().add(transferRequestDto.getAmount()));
        currentLedger.setBalance(currentLedger.getBalance().add(transferRequestDto.getAmount()));
        ledgerRepository.save(currentLedger);

        return transactionRepository.save(transaction);
    }

    /**
     * Creates an audit transaction for a payment account.
     *
     * @param paymentAccount The payment account.
     * @param newLedger The new ledger to be associated with the transaction.
     * @param balance The balance amount to be audited.
     * @return The created payment transaction.
     */
    public PaymentTransaction createAuditTransaction(PaymentAccount paymentAccount, Ledger newLedger, BigDecimal balance) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionFee(balance);
        transaction.setDescription("Audited Ledger balance");
        transaction.setPaymentAccount(paymentAccount);
        transaction.setTrxType(TrxType.FORWARD_BALANCE.getTrxType());
        transaction.setUser(paymentAccount.getUser());
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setDateCreated(LocalDateTime.now());
        transaction.setLedger(newLedger);

        return transactionRepository.save(transaction);
    }
}
