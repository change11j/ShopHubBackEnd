package org.ctc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Orders {
    @Id
    private String orderId;

    private Integer totalAmount;

    private String ecpayId;

    private Integer orderStatus;

    private Integer payMethod;

    private Date orderDate;
}
