package com.onebank.onebank.payment.service.implementation;

import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.auth.service.UserService;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.payment.dto.enums.TrxStatus;
import com.onebank.onebank.payment.dto.input.PaymentAccountRequestDto;
import com.onebank.onebank.payment.dto.input.TransferRequestDto;
import com.onebank.onebank.payment.dto.output.NameEnquiryDTO;
import com.onebank.onebank.payment.dto.output.PaymentAccountDTO;
import com.onebank.onebank.payment.dto.output.TransferResponseDTO;
import com.onebank.onebank.payment.entity.Ledger;
import com.onebank.onebank.payment.entity.PaymentAccount;
import com.onebank.onebank.payment.entity.PaymentTransaction;
import com.onebank.onebank.payment.repository.LedgerRepository;
import com.onebank.onebank.payment.repository.PaymentAccountRepository;
import com.onebank.onebank.payment.service.PaymentAccountService;
import com.onebank.onebank.payment.service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImp implements PaymentAccountService {

    private final PaymentAccountRepository paymentAccountRepository;
    private final LedgerRepository ledgerRepository;
    private final PaymentTransactionService transactionService;
    private final UserService userService;

    private static final String PREFIX = "9731";
    private static final Random random = new Random();
    private static final int ACCOUNT_NUMBER_LENGTH = 10;

    @Autowired
    public PaymentServiceImp(PaymentAccountRepository paymentAccountRepository, LedgerRepository ledgerRepository, @Lazy PaymentTransactionService transactionService, UserService userService) {
        this.paymentAccountRepository = paymentAccountRepository;
        this.ledgerRepository = ledgerRepository;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    /**
     * Retrieve all payment accounts for a given user.
     *
     * @param user the user whose accounts are to be retrieved
     * @return list of payment account DTOs
     */
    @Override
    public List<PaymentAccountDTO> getPaymentAccounts(AppUser user) {
        return paymentAccountRepository.findByUser(user).stream()
                .map(PaymentAccountDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Perform an intra-account transfer.
     *
     * @param transferRequestDto the transfer request data
     * @return the transfer response DTO
     */
    @Override
    public TransferResponseDTO intraTransfer(TransferRequestDto transferRequestDto) {
        try {
            PaymentAccount sourceAccount = paymentAccountRepository.findByAccountNumber(transferRequestDto.getSourceAccountNumber())
                    .orElseThrow(() -> new AccountNotFoundException("Source account not found"));
            PaymentAccount destinationAccount = paymentAccountRepository.findByAccountNumber(transferRequestDto.getDestinationAccountNumber())
                    .orElseThrow(() -> new AccountNotFoundException("Destination account not found"));

            PaymentTransaction sourceTransaction = transactionService.createSourceTransaction(sourceAccount, transferRequestDto);

            if (sourceTransaction.getStatus() == TrxStatus.SUCCESSFUL.getTrxStatus()) {
                transactionService.createDestinationTransaction(destinationAccount, transferRequestDto);
            }

            return new TransferResponseDTO(Status.SUCCESS, new TransferResponseDTO(sourceTransaction));
        } catch (AccountNotFoundException ex) {
            return new TransferResponseDTO(Status.NOT_PERMITTED, ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return new TransferResponseDTO(Status.NOT_PERMITTED, "Transfer failed due to an unexpected error");
        }
    }

    /**
     * Deposit funds into an account.
     *
     * @param transferRequestDto the deposit request data
     * @return the transfer response DTO
     */
    @Override
    public TransferResponseDTO deposit(TransferRequestDto transferRequestDto) {
        try {
            PaymentAccount destinationAccount = paymentAccountRepository.findByAccountNumber(transferRequestDto.getDestinationAccountNumber())
                    .orElseThrow(() -> new AccountNotFoundException("Destination account not found"));

            PaymentTransaction depositTransaction = transactionService.createDestinationTransaction(destinationAccount, transferRequestDto);

            return new TransferResponseDTO(Status.SUCCESS, new TransferResponseDTO(depositTransaction));
        } catch (AccountNotFoundException ex) {
            System.out.println(ex);
            return new TransferResponseDTO(Status.NOT_PERMITTED, ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return new TransferResponseDTO(Status.NOT_PERMITTED, "Deposit failed due to an unexpected error");
        }
    }

    /**
     * Find a payment account by ID.
     *
     * @param id the account ID
     * @return an optional payment account
     */
    @Override
    public Optional<PaymentAccount> findById(Long id) {
        return paymentAccountRepository.findById(id);
    }

    /**
     * Generate a unique account number.
     *
     * @return a generated account number
     */
    public static synchronized String generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder(PREFIX);
        int remainingDigits = ACCOUNT_NUMBER_LENGTH - PREFIX.length();
        long timestamp = Instant.now().toEpochMilli();
        String timestampStr = Long.toString(timestamp);
        String timestampPart = timestampStr.substring(Math.max(0, timestampStr.length() - remainingDigits));
        accountNumber.append(timestampPart);

        while (accountNumber.length() < ACCOUNT_NUMBER_LENGTH) {
            int digit = random.nextInt(10);
            accountNumber.append(digit);
        }

        return accountNumber.toString();
    }

    /**
     * Create a new payment account.
     *
     * @param paymentAccountRequestDto the payment account request data
     * @param username                 the username of the account owner
     * @return the created payment account DTO
     */
    @Override
    public PaymentAccountDTO createPaymentAccount(PaymentAccountRequestDto paymentAccountRequestDto, String username) {
        AppUser user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PaymentAccount paymentAccount = PaymentAccountRequestDto.fromDto(paymentAccountRequestDto);
        paymentAccount.setUser(user);
        paymentAccount.setAccountNumber(generateAccountNumber());
        paymentAccount.setDateCreated(LocalDateTime.now());

        paymentAccount = paymentAccountRepository.save(paymentAccount);

        Ledger ledger = new Ledger();
        ledger.setBalance(BigDecimal.ZERO);
        ledger.setCredit(BigDecimal.ZERO);
        ledger.setDebit(BigDecimal.ZERO);
        ledger.setPaymentAccount(paymentAccount);
        ledger.setClosed(false);
        ledger.setDateCreated(LocalDate.now());
        Ledger newLedger = ledgerRepository.save(ledger);
        paymentAccount.setCurrentLedger(newLedger);

        paymentAccount = paymentAccountRepository.save(paymentAccount);

        return new PaymentAccountDTO(paymentAccount);
    }

    /**
     * Validate NIP account.
     *
     * @param accountNumber the account number
     * @return the name enquiry DTO
     * @throws AccountNotFoundException if the account is not found
     */

    public NameEnquiryDTO nipaccountvalidate(String accountNumber) throws AccountNotFoundException {
        PaymentAccount paymentAccount = paymentAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Invalid account number"));
        return new NameEnquiryDTO(Status.SUCCESS, new NameEnquiryDTO(paymentAccount));
    }
}
