package org.ctc.controller;

import org.ctc.dto.Result;
import org.ctc.entity.BankAccount;
import org.ctc.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/bank")
public class BankAccountController {
    private BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/{id}")
    public Result getBankAccounts(@PathVariable Integer id){
        return bankAccountService.getAllBankAccountsBySellerId(id);
    }

    @PostMapping
    public Result addBankAccount(@RequestBody BankAccount bankAccount){
        return bankAccountService.saveBankAccount(bankAccount);
    }
}
