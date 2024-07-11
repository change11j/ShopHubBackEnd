package org.ctc.dto;

import lombok.Data;

@Data
public class OrderDetailDTO {
    private Integer productId;
    private String productName;
    private String imageUrl;
    private String spec;
    private String specName;
    private Integer price;
    private Integer quantity;
    private String ecpayId;
    private Integer buyerId;
    private String userName;
    private Integer orderStatus;
    private Integer shipMethod;

    public OrderDetailDTO(
            Integer productId,
            String productName,
            String imageUrl,
            String spec,
            String specName,
            Integer price,
            Integer quantity,
            String ecpayId,
            Integer buyerId,
            String userName,
            Integer orderStatus,
            Integer shipMethod
    ) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.spec = spec;
        this.specName = specName;
        this.price = price;
        this.quantity = quantity;
        this.ecpayId = ecpayId;
        this.buyerId = buyerId;
        this.userName = userName;
        this.orderStatus = orderStatus;
        this.shipMethod = shipMethod;
    }
}
