package org.ctc.service;


import org.ctc.dao.BankAccountDao;
import org.ctc.dao.OrderDetailDao;
import org.ctc.dto.BankAccountDTO;
import org.ctc.dto.Result;
import org.ctc.entity.BankAccount;
import org.ctc.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.ctc.costant.Constance.SUCCESS;

@Service
public class BankAccountService {
    private final BankAccountDao bankAccountDao;

    @Autowired
    public BankAccountService(BankAccountDao bankAccountDao) {
        this.bankAccountDao = bankAccountDao;
    }

    public Result getAllBankAccountsBySellerId(Integer sellerId){
        List<BankAccountDTO> bankAccounts=bankAccountDao.findAllBankAccountBySellerId(sellerId);

        return new Result(SUCCESS,bankAccounts);
    }

    public Result saveBankAccount(BankAccount bankAccount){
        BankAccount account=bankAccountDao.save(bankAccount);
        return new Result(SUCCESS,account);
    }
}
