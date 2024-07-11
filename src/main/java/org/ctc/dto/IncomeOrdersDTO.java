package org.ctc.dto;

import lombok.Data;

import java.util.Date;

@Data
public class IncomeOrdersDTO {
    private String ecpayId;
    private Date orderDate;
    private Integer orderStatus;
    private String payMethod;
    private Long totalAmount;

    public IncomeOrdersDTO(String ecpayId, Date orderDate, Integer orderStatus, String payMethod, Long totalAmount) {
        this.ecpayId = ecpayId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.payMethod = payMethod;
        this.totalAmount = totalAmount;
    }
}
