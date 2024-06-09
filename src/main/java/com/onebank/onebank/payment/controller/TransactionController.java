package com.onebank.onebank.payment.controller;

import com.onebank.onebank.basicController.Controller;
import com.onebank.onebank.payment.dto.output.TransactionResponseDTO;
import com.onebank.onebank.payment.service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController extends Controller {
    @Autowired
    public TransactionController(PaymentTransactionService transactionService) {

        this.transactionService = transactionService;
    }

    private PaymentTransactionService transactionService;

    @GetMapping
    public TransactionResponseDTO getTransactions(
            @RequestParam Long paymentAccountId,
            @RequestParam int page,
            @RequestParam int size) throws AccountNotFoundException {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return updateHttpStatus(transactionService.getTransactions(user.getUsername(), paymentAccountId, page, size));
    }

    @GetMapping("/filter")
    public TransactionResponseDTO filterTransactions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long paymentAccountId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) throws AccountNotFoundException {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return updateHttpStatus(transactionService.filterTransactions(user.getUsername(),status, paymentAccountId, startDate, endDate));
    }
}

