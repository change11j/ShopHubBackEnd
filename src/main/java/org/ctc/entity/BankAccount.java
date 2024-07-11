package org.ctc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Integer baId;
    private String bankCode;
    private String bankBranch;
    private String accountNumber;
    private String householdCounty;
    private String householdArea;
    private String householdAddress;
    private Integer sellerId;
}
