package org.ctc.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TotalAmountDTO {
    private Integer totalIncome;
    private Integer weeklyTotal;
    private Integer monthlyTotal;

    public TotalAmountDTO(Integer totalIncome, Integer weeklyTotal, Integer monthlyTotal) {
        this.totalIncome = totalIncome;
        this.weeklyTotal = weeklyTotal;
        this.monthlyTotal = monthlyTotal;
    }
}
