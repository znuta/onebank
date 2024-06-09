package com.onebank.onebank.payment.controller;

import com.onebank.onebank.basicController.Controller;
import com.onebank.onebank.payment.dto.input.PaymentAccountRequestDto;
import com.onebank.onebank.payment.dto.input.TransferRequestDto;
import com.onebank.onebank.payment.dto.output.LedgerDTO;
import com.onebank.onebank.payment.dto.output.NameEnquiryDTO;
import com.onebank.onebank.payment.dto.output.PaymentAccountDTO;
import com.onebank.onebank.payment.dto.output.TransferResponseDTO;


import com.onebank.onebank.payment.service.LedgerService;
import com.onebank.onebank.payment.service.PaymentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("/api/payment-account")
public class PaymentAccountController extends Controller {
    private PaymentAccountService paymentManagerService;

    private LedgerService ledgerService;

    @Autowired
    public PaymentAccountController(PaymentAccountService paymentManagerService, LedgerService ledgerService) {
        this.paymentManagerService = paymentManagerService;

        this.ledgerService = ledgerService;
    }

    @PostMapping("/create")
    public PaymentAccountDTO createPaymentAccount(@RequestBody PaymentAccountRequestDto paymentAccountRequestDto) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return updateHttpStatus(paymentManagerService.createPaymentAccount(paymentAccountRequestDto, user.getUsername()));
    }

    @PostMapping("/deposit")
    public TransferResponseDTO depositFund(@RequestBody TransferRequestDto transaction) {
        return  updateHttpStatus(paymentManagerService.deposit(transaction));
    }

    @PostMapping("/transfer")
    public TransferResponseDTO IntraTransfer(@RequestBody TransferRequestDto transaction) {
        return  updateHttpStatus(paymentManagerService.intraTransfer(transaction));
    }

    @GetMapping("/validate-accountnumber")
    public NameEnquiryDTO nipaccountvalidate(@RequestParam(required = true) String account_number) throws AccountNotFoundException {
        return  updateHttpStatus(paymentManagerService.nipaccountvalidate(account_number));
    }

    @GetMapping("/transaction/summary")
    public LedgerDTO getAccountLedgerByDate(@RequestParam(required = true) Long account_id, @RequestParam(required = true) String date ) throws AccountNotFoundException {
      UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  updateHttpStatus(ledgerService.getAccountLedgerByDate(account_id, date, user.getUsername()));
    }



}
