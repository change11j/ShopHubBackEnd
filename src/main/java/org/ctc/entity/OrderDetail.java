package org.ctc.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class OrderDetail {
    @Id
    private String odId;

    private Integer price;

    private String shopName;

    private Integer productId;

    private String productName;

    private Integer quantity;

    private Integer sellerId;

    private Integer buyerId;

    private Integer shipMethod;

    private String address;

    private Integer orderStatus;

    private Date orderDate;

    private String payMethod;

    private String ecpayId;

    private String specName;

    private String spec;

    private Integer specId;
}
