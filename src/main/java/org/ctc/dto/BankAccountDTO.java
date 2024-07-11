package org.ctc.dto;

import lombok.Data;

import java.util.Date;

@Data
public class BankAccountDTO {
    private String accountName;
    private String idNum;
    private Date birthday;
    private String householdCounty;
    private String householdArea;
    private String householdAddress;
    private String bankCode;
    private String bankBranch;
    private String accountNumber;

    public BankAccountDTO(String accountName, String idNum, Date birthday, String householdCounty, String householdArea, String householdAddress, String bankCode, String bankBranch, String accountNumber) {
        this.accountName = accountName;
        this.idNum = idNum;
        this.birthday = birthday;
        this.householdCounty = householdCounty;
        this.householdArea = householdArea;
        this.householdAddress = householdAddress;
        this.bankCode = bankCode;
        this.bankBranch = bankBranch;
        this.accountNumber = accountNumber;
    }
}
