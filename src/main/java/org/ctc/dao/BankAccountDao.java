package org.ctc.dao;

import org.ctc.dto.BankAccountDTO;
import org.ctc.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public interface BankAccountDao extends JpaRepository<BankAccount,Integer> {
    @Query(
            "SELECT new org.ctc.dto.BankAccountDTO(" +
                    "u.userName, u.idNum, u.birthday, " +
                    "b.householdCounty, b.householdArea, b.householdAddress, " +
                    "b.bankCode, b.bankBranch, b.accountNumber) " +
                    "FROM BankAccount b " +
                    "JOIN Users u ON u.userId = b.sellerId " +
                    "WHERE b.sellerId = :sellerId"
    )
    List<BankAccountDTO> findAllBankAccountBySellerId(@Param("sellerId") Integer sellerId);

}
